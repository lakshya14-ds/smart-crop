package com.smartcrop.view;

import com.smartcrop.model.CropRecommendation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

public class CropRecommendationsView {

    private final VBox root;

    public CropRecommendationsView(List<CropRecommendation> crops) {
        root = new VBox(12);
        root.getStyleClass().add("card");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Recommended Crops");
        title.getStyleClass().add("card-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label subtitle = new Label("Based on soil and weather conditions");
        subtitle.getStyleClass().add("muted-text");
        header.getChildren().addAll(title, spacer, subtitle);

        root.getChildren().add(header);

        for (CropRecommendation crop : crops) {
            root.getChildren().add(buildCropCard(crop));
        }
    }

    private VBox buildCropCard(CropRecommendation crop) {
        VBox card = new VBox(12);
        card.getStyleClass().add("crop-card");
        card.setPadding(new Insets(16));

        // Top row: name + badge + icon
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox nameBox = new VBox(4);
        Label nameLabel = new Label(crop.getName());
        nameLabel.getStyleClass().add("crop-name");

        Label badge = new Label(capitalize(crop.getSuitability()) + " Match");
        badge.getStyleClass().addAll("badge", "badge-" + crop.getSuitability());
        nameBox.getChildren().addAll(nameLabel, badge);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (crop.getSuitability().equals("excellent")) {
            Label checkIcon = new Label("✅");
            checkIcon.setStyle("-fx-font-size: 20px;");
            topRow.getChildren().addAll(nameBox, spacer, checkIcon);
        } else {
            topRow.getChildren().addAll(nameBox, spacer);
        }

        // Stats row
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
                buildStat("Expected Yield", crop.getExpectedYield()),
                buildStat("Growth Period", crop.getGrowthPeriod()),
                buildStat("Water Need", crop.getWaterRequirement())
        );
        statsRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Reasons
        VBox reasonsBox = new VBox(6);
        reasonsBox.setStyle("-fx-border-color: #e5e7eb; -fx-border-width: 1 0 0 0; -fx-padding: 12 0 0 0;");
        Label whyLabel = new Label("Why this crop:");
        whyLabel.getStyleClass().add("section-label");
        reasonsBox.getChildren().add(whyLabel);

        for (String reason : crop.getReasons()) {
            HBox reasonRow = new HBox(8);
            reasonRow.setAlignment(Pos.TOP_LEFT);
            Label bullet = new Label("•");
            bullet.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
            Label reasonLabel = new Label(reason);
            reasonLabel.getStyleClass().add("muted-text");
            reasonLabel.setWrapText(true);
            HBox.setHgrow(reasonLabel, Priority.ALWAYS);
            reasonRow.getChildren().addAll(bullet, reasonLabel);
            reasonsBox.getChildren().add(reasonRow);
        }

        card.getChildren().addAll(topRow, statsRow, reasonsBox);
        return card;
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
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public VBox getRoot() { return root; }
}
