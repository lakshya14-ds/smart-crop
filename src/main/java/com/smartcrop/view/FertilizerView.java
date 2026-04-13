package com.smartcrop.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class FertilizerView {

    private final VBox root;

    public FertilizerView(double nitrogen, double phosphorus, double potassium, String soilType) {
        root = new VBox(14);
        root.getStyleClass().add("card");

        Label title = new Label("Fertilizer Recommendations");
        title.getStyleClass().add("card-title");

        // NPK status row
        HBox npkRow = new HBox(12);
        npkRow.getChildren().addAll(
                buildNutrientCard("Nitrogen (N)", nitrogen, 40, 80),
                buildNutrientCard("Phosphorus (P)", phosphorus, 25, 60),
                buildNutrientCard("Potassium (K)", potassium, 35, 70)
        );
        npkRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Recommendations
        VBox recoBox = new VBox(10);
        recoBox.getStyleClass().add("info-card");
        recoBox.setPadding(new Insets(14));

        HBox recoHeader = new HBox(8);
        recoHeader.setAlignment(Pos.CENTER_LEFT);
        Label beakerIcon = new Label("🧪");
        beakerIcon.setStyle("-fx-font-size: 16px;");
        VBox recoTitleBox = new VBox(2);
        Label recoTitle = new Label("Recommended Fertilizer Application");
        recoTitle.getStyleClass().add("info-heading");
        Label recoSub = new Label("Based on " + soilType + " soil type");
        recoSub.getStyleClass().add("muted-text");
        recoTitleBox.getChildren().addAll(recoTitle, recoSub);
        recoHeader.getChildren().addAll(beakerIcon, recoTitleBox);

        recoBox.getChildren().add(recoHeader);

        if (nitrogen < 40) {
            recoBox.getChildren().add(buildDeficiencyBox("Nitrogen Deficiency",
                    "Apply 50-60 kg/ha of Urea (46-0-0) or 30-40 kg/ha of Ammonium Sulfate"));
        }
        if (phosphorus < 25) {
            recoBox.getChildren().add(buildDeficiencyBox("Phosphorus Deficiency",
                    "Apply 40-50 kg/ha of Single Super Phosphate (SSP) or 20-25 kg/ha of DAP"));
        }
        if (potassium < 35) {
            recoBox.getChildren().add(buildDeficiencyBox("Potassium Deficiency",
                    "Apply 30-40 kg/ha of Muriate of Potash (MOP) or Potassium Sulfate"));
        }
        if (nitrogen >= 40 && phosphorus >= 25 && potassium >= 35) {
            VBox balancedBox = new VBox(4);
            balancedBox.setStyle("-fx-background-color: #f0fdf4; -fx-border-color: #86efac; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10;");
            Label blbl = new Label("✅ Balanced Nutrition");
            blbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #166534;");
            Label btext = new Label("Maintain current nutrient levels with 20-25 kg/ha of NPK (10-10-10) complex fertilizer");
            btext.getStyleClass().add("muted-text");
            btext.setWrapText(true);
            balancedBox.getChildren().addAll(blbl, btext);
            recoBox.getChildren().add(balancedBox);
        }

        // Application guidelines
        VBox guidelinesBox = new VBox(8);
        guidelinesBox.getStyleClass().add("muted-bg-box");
        guidelinesBox.setPadding(new Insets(12));
        Label guideLabel = new Label("Application Guidelines");
        guideLabel.getStyleClass().add("section-label");
        guidelinesBox.getChildren().add(guideLabel);
        guidelinesBox.getChildren().addAll(
                buildBullet("🟣", "Apply fertilizers in split doses for better nutrient uptake"),
                buildBullet("🟣", "First dose: At planting or early vegetative stage"),
                buildBullet("🟣", "Second dose: During flowering or mid-growth stage"),
                buildBullet("🟣", "Irrigate after fertilizer application for proper dissolution")
        );

        root.getChildren().addAll(title, npkRow, recoBox, guidelinesBox);
    }

    private VBox buildNutrientCard(String label, double value, double low, double high) {
        VBox card = new VBox(6);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(12));

        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("muted-text");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String status;
        String statusStyle;
        String icon;
        if (value < low) {
            status = "Low";
            statusStyle = "-fx-text-fill: #dc2626;";
            icon = "📉";
        } else if (value > high) {
            status = "High";
            statusStyle = "-fx-text-fill: #ea580c;";
            icon = "📈";
        } else {
            status = "Optimal";
            statusStyle = "-fx-text-fill: #16a34a;";
            icon = "➖";
        }

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 14px;");
        headerRow.getChildren().addAll(lbl, spacer, iconLabel);

        Label valueLabel = new Label(value + " mg/kg");
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label statusLabel = new Label(status);
        statusLabel.setStyle(statusStyle + " -fx-font-size: 12px;");

        card.getChildren().addAll(headerRow, valueLabel, statusLabel);
        return card;
    }

    private VBox buildDeficiencyBox(String title, String text) {
        VBox box = new VBox(4);
        box.setStyle("-fx-background-color: #fef2f2; -fx-border-color: #fecaca; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10;");
        Label titleLabel = new Label("⚠ " + title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #991b1b;");
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("muted-text");
        textLabel.setWrapText(true);
        box.getChildren().addAll(titleLabel, textLabel);
        return box;
    }

    private HBox buildBullet(String icon, String text) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.TOP_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 11px;");
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("muted-text");
        textLabel.setWrapText(true);
        HBox.setHgrow(textLabel, Priority.ALWAYS);
        row.getChildren().addAll(iconLabel, textLabel);
        return row;
    }

    public VBox getRoot() { return root; }
}
