package com.smartcrop.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class IrrigationView {

    private final VBox root;

    public IrrigationView(double soilMoisture, double rainfall, double temperature) {
        root = new VBox(14);
        root.getStyleClass().add("card");

        Label title = new Label("Irrigation Advisory");
        title.getStyleClass().add("card-title");

        // Status box
        String statusText;
        String statusClass;
        String statusIcon;
        if (soilMoisture < 20) {
            statusText = "Immediate irrigation required";
            statusClass = "status-red";
            statusIcon = "🔴";
        } else if (soilMoisture < 35) {
            statusText = "Irrigation recommended soon";
            statusClass = "status-yellow";
            statusIcon = "🟡";
        } else {
            statusText = "Soil moisture adequate";
            statusClass = "status-green";
            statusIcon = "🟢";
        }

        VBox statusBox = new VBox(6);
        statusBox.getStyleClass().addAll("status-box", statusClass);
        statusBox.setPadding(new Insets(12));

        HBox statusRow = new HBox(8);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        Label statusIconLabel = new Label(statusIcon);
        statusIconLabel.setStyle("-fx-font-size: 18px;");
        Label statusLabel = new Label(statusText);
        statusLabel.getStyleClass().add("status-text-" + (soilMoisture < 20 ? "red" : soilMoisture < 35 ? "yellow" : "green"));
        statusRow.getChildren().addAll(statusIconLabel, statusLabel);

        Label moistureLabel = new Label("Current soil moisture: " + soilMoisture + "%");
        moistureLabel.getStyleClass().add("muted-text");
        statusBox.getChildren().addAll(statusRow, moistureLabel);

        // Schedule card
        VBox scheduleBox = buildInfoBox("📅", "Recommended Schedule",
                soilMoisture < 20
                        ? "Irrigate immediately and continue daily until moisture reaches 35–40%"
                        : soilMoisture < 35
                        ? "Irrigate within the next 24–48 hours, then monitor daily"
                        : "Continue monitoring. Next irrigation likely needed in 3–5 days");

        // Water requirement card
        String waterAmt = temperature > 30 ? "6–8 liters/m²" : temperature > 25 ? "4–6 liters/m²" : "3–4 liters/m²";
        VBox waterBox = buildInfoBox("📈", "Water Requirement",
                "Estimated: " + waterAmt + " per irrigation cycle");

        // Conditions analysis
        VBox conditionsBox = new VBox(8);
        conditionsBox.getStyleClass().add("muted-bg-box");
        conditionsBox.setPadding(new Insets(12));

        Label condLabel = new Label("Conditions Analysis");
        condLabel.getStyleClass().add("section-label");
        conditionsBox.getChildren().add(condLabel);

        conditionsBox.getChildren().addAll(
                buildBullet("💧", "Recent rainfall: " + rainfall + "mm (last 7 days)"),
                buildBullet("🌡", "Temperature: " + temperature + "°C" + (temperature > 30 ? " – High evaporation rate expected" : "")),
                buildBullet("⏰", "Best irrigation time: Early morning (6–8 AM) or evening (6–8 PM)")
        );

        root.getChildren().addAll(title, statusBox, scheduleBox, waterBox, conditionsBox);
    }

    private VBox buildInfoBox(String icon, String heading, String text) {
        VBox box = new VBox(6);
        box.getStyleClass().add("info-card");
        box.setPadding(new Insets(12));

        HBox headRow = new HBox(8);
        headRow.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px;");
        Label headingLabel = new Label(heading);
        headingLabel.getStyleClass().add("info-heading");
        headRow.getChildren().addAll(iconLabel, headingLabel);

        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("muted-text");
        textLabel.setWrapText(true);

        box.getChildren().addAll(headRow, textLabel);
        return box;
    }

    private HBox buildBullet(String icon, String text) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.TOP_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 13px;");
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("muted-text");
        textLabel.setWrapText(true);
        HBox.setHgrow(textLabel, Priority.ALWAYS);
        row.getChildren().addAll(iconLabel, textLabel);
        return row;
    }

    public VBox getRoot() { return root; }
}
