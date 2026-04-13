package com.smartcrop;

import com.smartcrop.model.CropRecommendation;
import com.smartcrop.model.SoilData;
import com.smartcrop.model.WeatherData;
import com.smartcrop.service.SupabaseService;
import com.smartcrop.view.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardApp {

    private final Stage stage;
    private final String userEmail;
    private SoilData soilData;
    private final WeatherData weatherData;
    private List<CropRecommendation> cropRecommendations;
    private BorderPane mainLayout;
    private HBox tabBar;
    private String activeTab = "overview";
    private final SupabaseService supabase = SupabaseService.getInstance();

    // Fallback crops used if Supabase is unreachable
    private static final List<CropRecommendation> FALLBACK_CROPS = List.of(
            new CropRecommendation("Rice", "excellent", "4-5 tons/ha", "120-150 days", "High",
                    List.of("Loamy soil provides ideal drainage and nutrient retention",
                            "Current pH level (6.8) is optimal for rice cultivation",
                            "Adequate moisture and favorable temperature conditions")),
            new CropRecommendation("Wheat", "good", "3-4 tons/ha", "110-130 days", "Moderate",
                    List.of("Soil nutrients support good grain development",
                            "Temperature range suitable for winter wheat")),
            new CropRecommendation("Maize", "good", "5-6 tons/ha", "90-120 days", "Moderate to High",
                    List.of("High nitrogen levels promote vegetative growth",
                            "Loamy soil provides good root development")));

    public DashboardApp(Stage stage, String userEmail) {
        this.stage = stage;
        this.userEmail = userEmail;

        soilData = new SoilData("loamy", 6.8, 55, 32, 45, 3.8, 28);
        cropRecommendations = new ArrayList<>(FALLBACK_CROPS);

        weatherData = new WeatherData(28, 65, 15, 12, "Partly Cloudy",
                List.of(
                        new WeatherData.ForecastDay("Wed", 29, "Sunny"),
                        new WeatherData.ForecastDay("Thu", 27, "Cloudy"),
                        new WeatherData.ForecastDay("Fri", 26, "Rain"),
                        new WeatherData.ForecastDay("Sat", 28, "Sunny"),
                        new WeatherData.ForecastDay("Sun", 30, "Sunny")));
    }

    public void show() {
        mainLayout = new BorderPane();
        mainLayout.setTop(buildHeader());

        VBox content = new VBox();
        content.getChildren().addAll(buildTabBar(), buildContentArea("overview"));
        mainLayout.setCenter(content);

        Scene scene = new Scene(mainLayout, 1100, 720);
        scene.getStylesheets().add(MainApp.class.getResource("/styles/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Smart Crop Advisory System");
        stage.show();

        // Load Supabase data in background after UI is shown
        loadSoilDataFromSupabase();
        loadCropsFromSupabase();
    }

    // ── Supabase loaders ──────────────────────────────────────────────────────

    private void loadSoilDataFromSupabase() {
        new Thread(() -> {
            try {
                String json = supabase.loadLatestSoilData(userEmail);
                JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
                if (arr.size() > 0) {
                    JsonObject row = arr.get(0).getAsJsonObject();
                    SoilData loaded = new SoilData(
                            row.has("soil_type") ? row.get("soil_type").getAsString() : "loamy",
                            row.has("ph") ? row.get("ph").getAsDouble() : 6.8,
                            row.has("nitrogen") ? row.get("nitrogen").getAsDouble() : 55,
                            row.has("phosphorus") ? row.get("phosphorus").getAsDouble() : 32,
                            row.has("potassium") ? row.get("potassium").getAsDouble() : 45,
                            row.has("organic_matter") ? row.get("organic_matter").getAsDouble() : 3.8,
                            row.has("moisture") ? row.get("moisture").getAsDouble() : 28);
                    Platform.runLater(() -> {
                        soilData = loaded;
                        refreshCurrentTab();
                    });
                }
            } catch (Exception e) {
                System.out.println("Could not load soil data: " + e.getMessage());
            }
        }).start();
    }

    private void loadCropsFromSupabase() {
        new Thread(() -> {
            try {
                String json = supabase.loadCrops();
                JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
                if (arr.size() > 0) {
                    List<CropRecommendation> loaded = new ArrayList<>();
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject row = arr.get(i).getAsJsonObject();
                        String name = row.has("name") ? row.get("name").getAsString() : "Unknown";
                        String suitability = row.has("suitability") ? row.get("suitability").getAsString() : "good";
                        String yield = row.has("expected_yield") ? row.get("expected_yield").getAsString() : "";
                        String period = row.has("growth_period") ? row.get("growth_period").getAsString() : "";
                        String water = row.has("water_requirement") ? row.get("water_requirement").getAsString()
                                : "Moderate";
                        // reasons stored as pipe-separated string
                        List<String> reasons = new ArrayList<>();
                        if (row.has("reasons") && !row.get("reasons").isJsonNull()) {
                            String raw = row.get("reasons").getAsString();
                            if (!raw.isBlank()) {
                                reasons = Arrays.asList(raw.split("\\|"));
                            }
                        }
                        loaded.add(new CropRecommendation(
                                name,
                                suitability,
                                yield,
                                period,
                                water,
                                reasons,
                                0 // or actual score if available
                        ));
                    }
                    Platform.runLater(() -> {
                        cropRecommendations = loaded;
                        refreshCurrentTab();
                    });
                }
            } catch (Exception e) {
                System.out.println("Could not load crops from Supabase (using fallback): " + e.getMessage());
            }
        }).start();
    }

    /** Re-renders the currently visible tab to reflect updated data */
    private void refreshCurrentTab() {
        VBox contentContainer = (VBox) mainLayout.getCenter();
        if (contentContainer.getChildren().size() > 1)
            contentContainer.getChildren().remove(1);
        contentContainer.getChildren().add(buildContentArea(activeTab));
    }

    // ── UI builders ───────────────────────────────────────────────────────────

    private HBox buildHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 24, 14, 24));
        header.setSpacing(12);

        Label iconLabel = new Label("🌱");
        iconLabel.setStyle("-fx-font-size: 24px;");

        VBox titleBox = new VBox(2);
        Label appTitle = new Label("Smart Crop Advisory");
        appTitle.getStyleClass().add("header-title");
        Label emailLabel = new Label(userEmail);
        emailLabel.getStyleClass().add("header-subtitle");
        titleBox.getChildren().addAll(appTitle, emailLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("⎋ Logout");
        logoutBtn.getStyleClass().add("btn-outline");
        logoutBtn.setOnAction(e -> {
            supabase.signOut();
            LoginView loginView = new LoginView(email -> new DashboardApp(stage, email).show());
            Scene scene = new Scene(loginView.getRoot(), 1100, 720);
            scene.getStylesheets().add(MainApp.class.getResource("/styles/app.css").toExternalForm());
            stage.setScene(scene);
        });

        header.getChildren().addAll(iconLabel, titleBox, spacer, logoutBtn);
        return header;
    }

    private HBox buildTabBar() {
        tabBar = new HBox(8);
        tabBar.getStyleClass().add("tab-bar");
        tabBar.setPadding(new Insets(12, 24, 0, 24));

        String[][] tabs = {
                { "overview", "🏠 Overview" },
                { "soil", "🌱 Soil Data" },
                { "weather", "🌤 Weather" },
                { "recommendations", "💡 Recommendations" },
                { "reports", "📄 Reports" }
        };

        for (String[] tab : tabs) {
            Button btn = new Button(tab[1]);
            btn.getStyleClass().add("tab-btn");
            if (tab[0].equals("overview"))
                btn.getStyleClass().add("tab-btn-active");
            btn.setUserData(tab[0]);
            btn.setOnAction(e -> switchTab(tab[0]));
            tabBar.getChildren().add(btn);
        }
        return tabBar;
    }

    private void switchTab(String tabId) {
        activeTab = tabId;
        tabBar.getChildren().forEach(node -> {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("tab-btn-active");
                if (tabId.equals(btn.getUserData()))
                    btn.getStyleClass().add("tab-btn-active");
            }
        });
        VBox contentContainer = (VBox) mainLayout.getCenter();
        if (contentContainer.getChildren().size() > 1)
            contentContainer.getChildren().remove(1);
        contentContainer.getChildren().add(buildContentArea(tabId));
    }

    private javafx.scene.Node buildContentArea(String tabId) {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("content-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        switch (tabId) {
            case "overview" -> {
                VBox overview = new VBox(16);
                overview.setPadding(new Insets(20, 24, 24, 24));
                HBox advisoryRow = new HBox(16);
                advisoryRow.getChildren().addAll(
                        new IrrigationView(soilData.getMoisture(), weatherData.getRainfall(),
                                weatherData.getTemperature()).getRoot(),
                        new FertilizerView(soilData.getNitrogen(), soilData.getPhosphorus(), soilData.getPotassium(),
                                soilData.getSoilType()).getRoot());
                HBox.setHgrow(advisoryRow.getChildren().get(0), Priority.ALWAYS);
                HBox.setHgrow(advisoryRow.getChildren().get(1), Priority.ALWAYS);
                overview.getChildren().addAll(
                        new WeatherView().getRoot(),
                        advisoryRow,
                        new CropRecommendationsView().getRoot());
                scroll.setContent(overview);
            }
            case "soil" -> {
                VBox soilContainer = new VBox();
                soilContainer.setPadding(new Insets(20, 24, 24, 24));
                soilContainer.setMaxWidth(720);
                soilContainer.getChildren().add(
                        new SoilDataView(userEmail, supabase, data -> {
                            soilData = data;
                            // Re-evaluate crop suitability against new soil values
                            refreshCurrentTab();
                        }).getRoot());
                scroll.setContent(soilContainer);
            }
            case "weather" -> {
                VBox wc = new VBox();
                wc.setPadding(new Insets(20, 24, 24, 24));
                wc.getChildren().add(new WeatherView().getRoot());
                scroll.setContent(wc);
            }
            case "recommendations" -> {
                VBox reco = new VBox(16);
                reco.setPadding(new Insets(20, 24, 24, 24));
                HBox advisoryRow = new HBox(16);
                advisoryRow.getChildren().addAll(
                        new IrrigationView(soilData.getMoisture(), weatherData.getRainfall(),
                                weatherData.getTemperature()).getRoot(),
                        new FertilizerView(soilData.getNitrogen(), soilData.getPhosphorus(), soilData.getPotassium(),
                                soilData.getSoilType()).getRoot());
                HBox.setHgrow(advisoryRow.getChildren().get(0), Priority.ALWAYS);
                HBox.setHgrow(advisoryRow.getChildren().get(1), Priority.ALWAYS);
                reco.getChildren().addAll(
                        new CropRecommendationsView().getRoot(),
                        advisoryRow);
                scroll.setContent(reco);
            }
            case "reports" -> {
                VBox rc = new VBox();
                rc.setPadding(new Insets(20, 24, 24, 24));
                rc.getChildren().add(new ReportsView(userEmail).getRoot());
                scroll.setContent(rc);
            }
        }
        return scroll;
    }
}
