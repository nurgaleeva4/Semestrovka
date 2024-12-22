package org.example.weatherapp;

import java.sql.*;

public class DatabaseService {

    private static final String URL = "jdbc:sqlite:my.db";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);

    };

    public DatabaseService() {
        createTable();
    }

    public void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS CITIES ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "country TEXT NOT NULL "
                + ");";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.execute();

        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public void insertCity (String city, String country) {
        String insertCitySQL = "INSERT INTO CITIES (name, country) VALUES (?, ?)";

        try { Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertCitySQL);
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, country);
            preparedStatement.executeUpdate();
            connection.commit();
            connection.close();

        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public void deleteAllRows() {
        String deleteAllRowsSQL = "DELETE FROM CITIES;";
        try { Connection connection = DriverManager.getConnection(URL);
            PreparedStatement preparedStatement = connection.prepareStatement(deleteAllRowsSQL);
            preparedStatement.executeUpdate();
            connection.commit();
            connection.close();

        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public String getCityName() {
        String query = "SELECT name FROM CITIES LIMIT 1";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasCityRecord() {
        String query = "SELECT COUNT(*) FROM CITIES";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
