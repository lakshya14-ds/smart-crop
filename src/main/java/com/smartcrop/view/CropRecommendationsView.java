package com.smartcrop.view;

import com.smartcrop.model.CropRecommendation;
import com.smartcrop.service.RecommendationEngine;
import com.smartcrop.service.SupabaseService;
import com.smartcrop.service.WeatherService;
import com.google.gson.JsonObject;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class CropRecommendationsView {

    private final VBox root;
    private final HBox header;

    public CropRecommendationsView() {

        root = new VBox(12);
        root.getStyleClass().add("card");

        header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Recommended Crops");
        title.getStyleClass().add("card-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label subtitle = new Label("Based on soil + weather intelligence");
        subtitle.getStyleClass().add("muted-text");

        header.getChildren().addAll(title, spacer, subtitle);
        root.getChildren().add(header);

        loadRecommendations();
    }

    private void loadRecommendations() {

        new Thread(() -> {
            try {
                SupabaseService db = SupabaseService.getInstance();
                JsonObject soil = db.getLatestSoilDataParsed("lakshya123cms@gmail.com");

                if (soil == null) {
                    showError("No soil data found. Please save soil data first.");
                    return;
                }

                WeatherService ws = new WeatherService();
                JsonObject weather = ws.getWeather("Bengaluru");

                if (weather == null) {
                    showError("Weather data not available.");
                    return;
                }

                RecommendationEngine engine = new RecommendationEngine();
                List<CropRecommendation> crops = engine.generate(soil, weather);

                Platform.runLater(() -> {
                    root.getChildren().clear();
                    root.getChildren().add(header);

                    if (crops == null || crops.isEmpty()) {
                        Label empty = new Label("No recommendations available.");
                        root.getChildren().add(empty);
                        return;
                    }

                    for (CropRecommendation crop : crops) {
                        root.getChildren().add(buildCropCard(crop));
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                showError("Failed to load recommendations.");
            }
        }).start();
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            root.getChildren().clear();
            root.getChildren().add(header);

            Label error = new Label("❌ " + message);
            error.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            root.getChildren().add(error);
        });
    }

    private HBox buildCropCard(CropRecommendation crop) {

        HBox container = new HBox(20);
        container.getStyleClass().add("crop-card");
        container.setPadding(new Insets(16));

        VBox left = new VBox(10);

        Label name = new Label(crop.getName());
        name.getStyleClass().add("crop-name");

        Label badge = new Label(capitalize(crop.getSuitability()) + " Match");
        badge.getStyleClass().addAll("badge", "badge-" + crop.getSuitability());

        Label score = new Label("🌱 " + crop.getScore() + "% Match");

        HBox stats = new HBox(16);
        stats.getChildren().addAll(
                buildStat("Yield", crop.getExpectedYield()),
                buildStat("Period", crop.getGrowthPeriod()),
                buildStat("Water", crop.getWaterRequirement()));

        VBox reasonsBox = new VBox(5);
        Label why = new Label("Why this crop:");
        why.getStyleClass().add("section-label");

        reasonsBox.getChildren().add(why);

        for (String r : crop.getReasons()) {
            Label l = new Label("• " + r);
            l.getStyleClass().add("muted-text");
            l.setWrapText(true);
            reasonsBox.getChildren().add(l);
        }

        left.getChildren().addAll(name, badge, score, stats, reasonsBox);

        // ✅ SAFE IMAGE LOADING
        Image img;
        try {
            var stream = getClass().getResourceAsStream(getCropImage(crop.getName()));
            if (stream != null) {
                img = new Image(stream);
            } else {
                img = new Image(getClass().getResourceAsStream("/images/default.jpg"));
            }
        } catch (Exception e) {
            img = new Image(getClass().getResourceAsStream("/images/default.jpg"));
        }

        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(true);

        VBox right = new VBox(imageView);
        right.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        container.getChildren().addAll(left, spacer, right);

        return container;
    }

    private VBox buildStat(String label, String value) {
        VBox box = new VBox(4);

        Label lbl = new Label(label);
        lbl.getStyleClass().add("muted-text");

        Label val = new Label(value);
        val.getStyleClass().add("stat-inline");

        box.getChildren().addAll(lbl, val);
        return box;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty())
            return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ✅ FIXED PATHS
    private String getCropImage(String cropName) {
        return switch (cropName.toLowerCase()) {
            case "rice" -> "/crop images/rice.jpg";
            case "maize" -> "/crop images/maize.jpg";
            case "wheat" -> "/crop images/wheat.jpg";
            case "pulses" -> "/crop images/pulses.jpg";
            default -> "/crop images/default.jpg";
        };
    }

    public VBox getRoot() {
        return root;
    }
}