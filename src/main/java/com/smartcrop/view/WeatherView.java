package com.smartcrop.view;

import com.smartcrop.service.WeatherService;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.control.Separator;

public class WeatherView {

    private final VBox root;

    private Label tempValue = new Label("-");
    private Label humidityValue = new Label("-");
    private Label rainfallValue = new Label("-");
    private Label windValue = new Label("-");

    public WeatherView() {

        root = new VBox(16);
        root.getStyleClass().add("card");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Current Weather (Bengaluru)");
        title.getStyleClass().add("card-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label updated = new Label("Fetching live data...");
        updated.getStyleClass().add("muted-text");

        header.getChildren().addAll(title, spacer, updated);

        // Stats row
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
                buildStat("🌡", "Temperature", tempValue, "stat-orange"),
                buildStat("💧", "Humidity", humidityValue, "stat-blue"),
                buildStat("🌧", "Rainfall", rainfallValue, "stat-cyan"),
                buildStat("💨", "Wind Speed", windValue, "stat-gray"));

        statsRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // ✅ FIXED FORECAST SECTION
        VBox forecastBox = new VBox(8);

        Label forecastTitle = new Label("5-Day Forecast");
        forecastTitle.getStyleClass().add("section-label");

        HBox forecastRow = new HBox(8); // 🔥 IMPORTANT

        forecastBox.getChildren().addAll(forecastTitle, forecastRow);

        root.getChildren().addAll(header, statsRow, new Separator(), forecastBox);

        // API calls
        loadWeather(updated);
        loadForecast(forecastRow); // 🔥 THIS WAS MISSING
    }

    // 🔥 CURRENT WEATHER
    private void loadWeather(Label updatedLabel) {

        new Thread(() -> {
            try {
                WeatherService ws = new WeatherService();
                JsonObject data = ws.getWeather("Bengaluru");

                double temp = data.getAsJsonObject("main").get("temp").getAsDouble();
                double humidity = data.getAsJsonObject("main").get("humidity").getAsDouble();
                double wind = data.getAsJsonObject("wind").get("speed").getAsDouble();

                double rain = 0;
                if (data.has("rain") && data.getAsJsonObject("rain").has("1h")) {
                    rain = data.getAsJsonObject("rain").get("1h").getAsDouble();
                }

                double finalRain = rain;

                Platform.runLater(() -> {
                    tempValue.setText(temp + " °C");
                    humidityValue.setText(humidity + " %");
                    rainfallValue.setText(finalRain + " mm");
                    windValue.setText(wind + " km/h");
                    updatedLabel.setText("Last updated: Just now");
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 🔥 5-DAY FORECAST
    private void loadForecast(HBox forecastRow) {

        new Thread(() -> {
            try {
                WeatherService ws = new WeatherService();
                JsonObject data = ws.getForecast("Bengaluru");

                JsonArray list = data.getAsJsonArray("list");

                Platform.runLater(() -> forecastRow.getChildren().clear());

                for (int i = 0; i < list.size(); i += 8) {

                    JsonObject item = list.get(i).getAsJsonObject();

                    double temp = item.getAsJsonObject("main").get("temp").getAsDouble();
                    String dateTime = item.get("dt_txt").getAsString();

                    String day = dateTime.substring(0, 10);

                    VBox dayBox = new VBox(6);
                    dayBox.getStyleClass().add("forecast-day");
                    dayBox.setAlignment(Pos.CENTER);
                    dayBox.setPadding(new Insets(10));

                    Label dayLabel = new Label(day);
                    Label iconLabel = new Label("🌤");
                    Label tempLabel = new Label(temp + "°C");

                    dayBox.getChildren().addAll(dayLabel, iconLabel, tempLabel);

                    VBox finalDayBox = dayBox;

                    Platform.runLater(() -> forecastRow.getChildren().add(finalDayBox));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private HBox buildStat(String emoji, String label, Label valueLabel, String colorClass) {

        HBox box = new HBox(10);
        box.getStyleClass().addAll("stat-box", colorClass);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(12));

        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 22px;");

        VBox textBox = new VBox(2);

        Label labelEl = new Label(label);
        labelEl.getStyleClass().add("muted-text");

        valueLabel.getStyleClass().add("stat-value");

        textBox.getChildren().addAll(labelEl, valueLabel);

        box.getChildren().addAll(emojiLabel, textBox);
        return box;
    }

    public VBox getRoot() {
        return root;
    }
}