module org.example.weatherapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires static lombok;
    requires java.sql;


    opens org.example.weatherapp to javafx.fxml;
    exports org.example.weatherapp;

}