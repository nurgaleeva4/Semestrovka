package org.example.weatherapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.lang.Math.round;

public class Main {

    @Setter
    private Stage stage;

    @FXML
    private Button cityNameButton;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label windSpeedLabel;

    @FXML
    private Label windDirectionLabel;

    @FXML
    private Label humidityLabel;

    @FXML
    private Label visibilityLabel;

    @FXML
    private Label pressureLabel;

    @FXML
    private Label feelsLikeLabel;

    @FXML
    private Label dateLabel1;

    @FXML
    private Label dateLabel2;

    @FXML
    private Label dateLabel3;

    @FXML
    private Label dateLabel4;

    @FXML
    private Label forecastTemperatureLabel1;

    @FXML
    private Label forecastTemperatureLabel2;

    @FXML
    private Label forecastTemperatureLabel3;

    @FXML
    private Label forecastTemperatureLabel4;

    @FXML
    private ImageView weatherIcon;

    @FXML
    private ImageView weatherIcon1;

    @FXML
    private ImageView weatherIcon2;

    @FXML
    private ImageView weatherIcon3;

    @FXML
    private ImageView weatherIcon4;

    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM");

    private final WeatherService weatherService = new WeatherService();
    private final DatabaseService databaseService = new DatabaseService();
    private final WeatherForecast weatherForecastService = new WeatherForecast();

    @FXML
    public void initialize() {
        fetchWeather();
    }

    private void fetchWeather() {

        new Thread(() -> {
            JSONObject weatherData = weatherService.getWeatherData(databaseService.getCityName().trim());
            JSONArray forecastData = weatherForecastService.getForecastData(databaseService.getCityName().trim());

            if (weatherData != null) {
                Platform.runLater(() -> updateUI(weatherData));
            } else {
                Platform.runLater(() -> cityNameButton.setText("Город не найден!"));
            }

            if (forecastData != null) {
                Platform.runLater(() -> updateForecastUI(forecastData));
            } else {
                //dateLabel.setText("Ошибка загрузки прогноза!");
            }
        }).start();
    }

    private void updateUI(JSONObject weatherData) {
        try {
            System.out.println(weatherData);
            String cityName = weatherData.getString("name");
            int temperature = (int) weatherData.getJSONObject("main").getDouble("temp");
            String description = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
            int windSpeed = (int) weatherData.getJSONObject("wind").getDouble("speed");
            String windDirection = getWindDirection(weatherData.getJSONObject("wind").getInt("deg"));
            int humidity = (int) weatherData.getJSONObject("main").getDouble("humidity");
            int visibility = (int) weatherData.getDouble("visibility") / 1000;
            double pressure = round(weatherData.getJSONObject("main").getDouble("pressure") * 0.7500638);
            int feelsLike = (int) weatherData.getJSONObject("main").getDouble("feels_like");

            cityNameButton.setText("Город: " + cityName);
            temperatureLabel.setText(temperature + " °C");
            descriptionLabel.setText("Погода: " + description);
            windSpeedLabel.setText(windSpeed + " м/с");
            windDirectionLabel.setText(windDirection);
            humidityLabel.setText(humidity + "%");
            visibilityLabel.setText(visibility + " км");
            pressureLabel.setText(pressure + " мм рт. ст.");
            feelsLikeLabel.setText(feelsLike + " °C");
            weatherIcon.setImage(getWeatherIcon(description));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateForecastUI(JSONArray forecastData) {
        StringBuilder forecastText = new StringBuilder();
        
        Label[] dateLabels = {dateLabel1, dateLabel2, dateLabel3, dateLabel4};
        Label[] tempLabels = {forecastTemperatureLabel1, forecastTemperatureLabel2, forecastTemperatureLabel3, forecastTemperatureLabel4};
        ImageView[] iconLabels = {weatherIcon1, weatherIcon2, weatherIcon3, weatherIcon4};

        try {
            for (int i = 1; i < 5; i++) {

                JSONObject dailyForecast = forecastData.getJSONObject(i * 8);
                LocalDate date = LocalDate.parse(dailyForecast.getString("dt_txt").split(" ")[0]);
                int temp = (int) dailyForecast.getJSONObject("main").getDouble("temp");
                String description = dailyForecast.getJSONArray("weather").getJSONObject(0).getString("description");

                forecastText.append(String.format("%s: %d°C, %s%n", date, temp, description));


                dateLabels[i-1].setText(date.format(outputFormatter));
                iconLabels[i-1].setImage(getWeatherIcon(description));
                tempLabels[i-1].setText(String.valueOf(temp));
            }
            System.out.println(forecastText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void changeCity() throws IOException {
        databaseService.deleteAllRows();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("CityFindingScene.fxml")));
        stage = (Stage) cityNameButton.getScene().getWindow();
        stage.setScene(new Scene(root, 800, 400));
    }

    private String getWindDirection(int degree) {
        if (degree >= 337.5 || degree < 22.5) {
            return "Север";
        } else if (degree >= 22.5 && degree < 67.5) {
            return "Северо-Восток";
        } else if (degree >= 67.5 && degree < 112.5) {
            return "Восток";
        } else if (degree >= 112.5 && degree < 157.5) {
            return "Юго-Восток";
        } else if (degree >= 157.5 && degree < 202.5) {
            return "Юг";
        } else if (degree >= 202.5 && degree < 247.5) {
            return "Юго-Запад";
        } else if (degree >= 247.5 && degree < 292.5) {
            return "Запад";
        } else {
            return "Северо-Запад";
        }
    }

    private Image getWeatherIcon(String description) {

        return switch (description.toLowerCase()) {
            case "ясно" -> new Image(Objects.requireNonNull(getClass().getResourceAsStream("sunny.png")));
            case "пасмурно" -> new Image(Objects.requireNonNull(getClass().getResourceAsStream("cloudy.png")));
            case "переменная облачность" -> new Image(Objects.requireNonNull(getClass().getResourceAsStream("cloudy_at_sometimes.png")));
            case "облачно с прояснениями" -> new Image(Objects.requireNonNull(getClass().getResourceAsStream("partly_cloudy.png")));
            case "небольшой дождь" -> new Image(Objects.requireNonNull(getClass().getResourceAsStream("light_rainy.png")));
            case "дождь" -> new Image(Objects.requireNonNull(getClass().getResourceAsStream("rainy.png")));
            case "небольшой снег" -> new Image(Objects.requireNonNull(getClass().getResourceAsStream("snowy.png")));
            default -> new Image(Objects.requireNonNull(getClass().getResourceAsStream("default.jpg")));
        };
    }
}

