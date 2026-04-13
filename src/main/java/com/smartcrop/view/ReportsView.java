package com.smartcrop.view;

import com.smartcrop.service.*;
import com.smartcrop.model.CropRecommendation;
import com.google.gson.*;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

public class ReportsView {

    private final VBox root;
    private final VBox reportsList = new VBox(8);
    private final Label statusLabel = new Label();
    private final String userEmail;
    private final SupabaseService supabase;

    public ReportsView(String userEmail) {
        this.userEmail = userEmail;
        this.supabase = SupabaseService.getInstance();

        root = new VBox(16);
        root.getStyleClass().add("card");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Reports");
        title.getStyleClass().add("card-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button generateBtn = new Button("📄 Generate New Report");
        generateBtn.getStyleClass().add("btn-primary");

        generateBtn.setOnAction(e ->
                generateReport("Comprehensive Report", "Comprehensive")
        );

        header.getChildren().addAll(title, spacer, generateBtn);

        // Cards
        HBox reportTypesRow = new HBox(12);
        reportTypesRow.getChildren().addAll(
                buildTypeCard("Comprehensive Report", "Complete soil, weather, and crop analysis", "Comprehensive"),
                buildTypeCard("Soil Report Only", "Detailed soil data", "Soil"),
                buildTypeCard("Advisory Report", "Irrigation & fertilizer advice", "Advisory")
        );

        statusLabel.getStyleClass().add("muted-text");

        Label recentLabel = new Label("Recent Reports");
        recentLabel.getStyleClass().add("section-label");

        root.getChildren().addAll(header, reportTypesRow, new Separator(),
                recentLabel, statusLabel, reportsList);

        loadReports();
    }

    // 🔥 LOAD REPORTS
    private void loadReports() {
        new Thread(() -> {
            try {
                String json = supabase.loadReports(userEmail);
                JsonArray arr = JsonParser.parseString(json).getAsJsonArray();

                Platform.runLater(() -> {
                    reportsList.getChildren().clear();

                    if (arr.size() == 0) {
                        reportsList.getChildren().add(new Label("No reports yet"));
                    } else {
                        for (int i = 0; i < arr.size(); i++) {
                            JsonObject row = arr.get(i).getAsJsonObject();

                            String title = row.get("title").getAsString();
                            String type = row.get("type").getAsString();
                            String date = row.get("created_at").getAsString().substring(0, 10);

                            JsonObject soil = row.has("soil") ? row.getAsJsonObject("soil") : new JsonObject();
                            JsonObject weather = row.has("weather") ? row.getAsJsonObject("weather") : new JsonObject();
                            JsonArray recs = row.has("recommendations") ? row.getAsJsonArray("recommendations") : new JsonArray();

                            reportsList.getChildren().add(
                                    buildReportRow(title, date, type, soil, weather, recs)
                            );
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 🔥 GENERATE REPORT
    private void generateReport(String title, String type) {

        statusLabel.setText("Generating report...");

        new Thread(() -> {
            try {

                // 1. Soil
                JsonObject soil = supabase.getLatestSoilDataParsed("lakshya123cms@gmail.com");

                if (soil == null) throw new Exception("No soil data found");

                // 2. Weather
                WeatherService ws = new WeatherService();
                JsonObject weather = ws.getWeather("Bengaluru");

                // 3. Recommendations
                RecommendationEngine engine = new RecommendationEngine();
                List<CropRecommendation> crops = engine.generate(soil, weather);

                // 4. Convert → JSON
                JsonArray cropArray = new JsonArray();
                for (CropRecommendation c : crops) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("name", c.getName());
                    obj.addProperty("score", c.getScore());
                    obj.addProperty("suitability", c.getSuitability());
                    cropArray.add(obj);
                }

                // 5. Build report
                JsonObject report = new JsonObject();
                report.addProperty("user_email", userEmail);
                report.addProperty("title", title + " - " + LocalDate.now());
                report.addProperty("type", type);
                report.add("soil", soil);
                report.add("weather", weather);
                report.add("recommendations", cropArray);

                // 6. Save
                supabase.insertRow("reports", report.toString());

                Platform.runLater(() -> {
                    statusLabel.setText("✅ Report generated successfully!");
                    loadReports();
                });

            } catch (Exception e) {
                e.printStackTrace();

                Platform.runLater(() -> {
                    statusLabel.setText("❌ Failed: " + e.getMessage());
                });
            }
        }).start();
    }

    private VBox buildTypeCard(String title, String desc, String type) {

        VBox card = new VBox(8);
        card.getStyleClass().add("report-type-card");
        card.setPadding(new Insets(14));

        Label titleLabel = new Label(title);
        Label descLabel = new Label(desc);

        Hyperlink link = new Hyperlink("Generate →");
        link.setOnAction(e -> generateReport(title, type));

        card.getChildren().addAll(titleLabel, descLabel, link);
        return card;
    }

    // 🔥 DOWNLOAD PDF HERE
    private HBox buildReportRow(String title, String date, String type,
                               JsonObject soil, JsonObject weather, JsonArray recs) {

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(title + " (" + date + ")");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button download = new Button("Download");

        download.setOnAction(e -> {
            try {
                String file = PdfService.generateReportPdf(title, soil, weather, recs);

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("✅ PDF saved at:\n" + file);
                a.show();

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("❌ Failed to generate PDF");
                a.show();
            }
        });

        row.getChildren().addAll(label, spacer, download);
        return row;
    }

    public VBox getRoot() {
        return root;
    }
}