package com.smartcrop.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ReportsView {

    private final VBox root;

    record Report(String id, String title, String date, String type) {}

    public ReportsView() {
        root = new VBox(16);
        root.getStyleClass().add("card");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Reports");
        title.getStyleClass().add("card-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button generateBtn = new Button("📄  Generate New Report");
        generateBtn.getStyleClass().add("btn-primary");
        generateBtn.setOnAction(e -> showInfo("Report generation will create a comprehensive PDF with all current data."));
        header.getChildren().addAll(title, spacer, generateBtn);

        // Report type cards
        HBox reportTypesRow = new HBox(12);
        reportTypesRow.getChildren().addAll(
                buildTypeCard("Comprehensive Report", "Complete soil, weather, and crop analysis"),
                buildTypeCard("Soil Report Only", "Detailed soil composition and nutrient levels"),
                buildTypeCard("Advisory Report", "Irrigation and fertilizer recommendations")
        );
        reportTypesRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Recent reports
        Label recentLabel = new Label("Recent Reports");
        recentLabel.getStyleClass().add("section-label");
        recentLabel.setStyle("-fx-padding: 8 0 0 0;");

        List<Report> reports = List.of(
                new Report("1", "Soil Analysis Report - April 2026", "2026-04-08", "Soil Analysis"),
                new Report("2", "Crop Recommendation Report - March 2026", "2026-03-15", "Crop Recommendations"),
                new Report("3", "Irrigation & Fertilizer Plan - March 2026", "2026-03-10", "Advisory Report")
        );

        VBox reportsList = new VBox(8);
        for (Report report : reports) {
            reportsList.getChildren().add(buildReportRow(report));
        }

        // Demo info
        VBox demoBox = new VBox(4);
        demoBox.getStyleClass().add("info-box-blue");
        demoBox.setPadding(new Insets(12));
        Label demoLabel = new Label("Demo Mode: Report history shown with sample data. Connect Supabase to generate and store actual reports.");
        demoLabel.getStyleClass().add("info-text-blue");
        demoLabel.setWrapText(true);
        demoBox.getChildren().add(demoLabel);

        root.getChildren().addAll(header, reportTypesRow, new Separator(), recentLabel, reportsList, demoBox);
    }

    private VBox buildTypeCard(String title, String desc) {
        VBox card = new VBox(8);
        card.getStyleClass().add("report-type-card");
        card.setPadding(new Insets(14));
        card.setStyle("-fx-cursor: hand; " + card.getStyle());

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("info-heading");

        Label descLabel = new Label(desc);
        descLabel.getStyleClass().add("muted-text");
        descLabel.setWrapText(true);

        Hyperlink genLink = new Hyperlink("Generate →");
        genLink.setStyle("-fx-text-fill: #16a34a;");
        genLink.setOnAction(e -> showInfo("Generating: " + title));

        card.getChildren().addAll(titleLabel, descLabel, genLink);
        return card;
    }

    private HBox buildReportRow(Report report) {
        HBox row = new HBox(12);
        row.getStyleClass().add("report-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));

        Label iconLabel = new Label("📄");
        iconLabel.setStyle("-fx-font-size: 20px; -fx-background-color: #dbeafe; -fx-padding: 6; -fx-background-radius: 6;");

        VBox info = new VBox(3);
        Label titleLabel = new Label(report.title());
        titleLabel.getStyleClass().add("info-heading");
        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label dateLabel = new Label("📅 " + report.date());
        dateLabel.getStyleClass().add("muted-text");
        Label dot = new Label("•");
        dot.getStyleClass().add("muted-text");
        Label typeLabel = new Label(report.type());
        typeLabel.getStyleClass().add("muted-text");
        meta.getChildren().addAll(dateLabel, dot, typeLabel);
        info.getChildren().addAll(titleLabel, meta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button downloadBtn = new Button("⬇  Download");
        downloadBtn.getStyleClass().add("btn-outline");
        downloadBtn.setOnAction(e -> showInfo("Downloading: " + report.title()));

        row.getChildren().addAll(iconLabel, info, spacer, downloadBtn);
        return row;
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Smart Crop Advisory");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public VBox getRoot() { return root; }
}
