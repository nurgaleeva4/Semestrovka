package org.example.weatherapp;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast {

    private static final String API_URL = "http://api.openweathermap.org/data/2.5/forecast";
    private static final String API_KEY = "e10e51844869344ddcbd60df424ee7b5";

    public JSONArray getForecastData(String cityName) {
        try {

            String urlString = String.format("%s?q=%s&appid=%s&units=metric&lang=ru", API_URL, cityName.replace(" ", "%20"), API_KEY);

            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                return jsonResponse.getJSONArray("list");
            } else {
                System.out.println("Ошибка: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}