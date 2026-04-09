package com.smartcrop.view;

import com.smartcrop.model.WeatherData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.control.Separator;

public class WeatherView {

    private final VBox root;

    public WeatherView(WeatherData data) {
        root = new VBox(16);
        root.getStyleClass().add("card");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Current Weather");
        title.getStyleClass().add("card-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label updated = new Label("Last updated: Just now");
        updated.getStyleClass().add("muted-text");
        header.getChildren().addAll(title, spacer, updated);

        // Stats grid
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
                buildStat("🌡", "Temperature", data.getTemperature() + "°C", "stat-orange"),
                buildStat("💧", "Humidity", data.getHumidity() + "%", "stat-blue"),
                buildStat("🌧", "Rainfall", data.getRainfall() + "mm", "stat-cyan"),
                buildStat("💨", "Wind Speed", data.getWindSpeed() + " km/h", "stat-gray")
        );
        statsRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Forecast
        VBox forecastBox = new VBox(8);
        Label forecastTitle = new Label("5-Day Forecast");
        forecastTitle.getStyleClass().add("section-label");

        HBox forecastRow = new HBox(8);
        for (WeatherData.ForecastDay day : data.getForecast()) {
            VBox dayBox = new VBox(6);
            dayBox.getStyleClass().add("forecast-day");
            dayBox.setAlignment(Pos.CENTER);
            dayBox.setPadding(new Insets(10));

            Label dayLabel = new Label(day.getDay());
            dayLabel.getStyleClass().add("forecast-day-label");

            String icon = getWeatherIcon(day.getCondition());
            Label iconLabel = new Label(icon);
            iconLabel.setStyle("-fx-font-size: 20px;");

            Label tempLabel = new Label(day.getTemp() + "°C");
            tempLabel.getStyleClass().add("forecast-temp");

            dayBox.getChildren().addAll(dayLabel, iconLabel, tempLabel);
            HBox.setHgrow(dayBox, Priority.ALWAYS);
            forecastRow.getChildren().add(dayBox);
        }
        forecastBox.getChildren().addAll(forecastTitle, forecastRow);

        root.getChildren().addAll(header, statsRow, new Separator(), forecastBox);
    }

    private HBox buildStat(String emoji, String label, String value, String colorClass) {
        HBox box = new HBox(10);
        box.getStyleClass().addAll("stat-box", colorClass);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(12));

        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 22px;");

        VBox textBox = new VBox(2);
        Label labelEl = new Label(label);
        labelEl.getStyleClass().add("muted-text");
        Label valueEl = new Label(value);
        valueEl.getStyleClass().add("stat-value");
        textBox.getChildren().addAll(labelEl, valueEl);

        box.getChildren().addAll(emojiLabel, textBox);
        return box;
    }

    private String getWeatherIcon(String condition) {
        return switch (condition.toLowerCase()) {
            case "sunny" -> "☀";
            case "cloudy" -> "☁";
            case "rain" -> "🌧";
            case "partly cloudy" -> "⛅";
            default -> "🌤";
        };
    }

    public VBox getRoot() { return root; }
}
