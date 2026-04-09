package com.smartcrop;

import com.smartcrop.model.SoilData;
import com.smartcrop.model.WeatherData;
import com.smartcrop.model.CropRecommendation;
import com.smartcrop.view.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import java.util.List;

public class DashboardApp {

    private final Stage stage;
    private final String userEmail;
    private SoilData soilData;
    private final WeatherData weatherData;
    private final List<CropRecommendation> cropRecommendations;
    private BorderPane mainLayout;
    private HBox tabBar;

    public DashboardApp(Stage stage, String userEmail) {
        this.stage = stage;
        this.userEmail = userEmail;

        // Default soil data
        soilData = new SoilData("loamy", 6.8, 55, 32, 45, 3.8, 28);

        // Mock weather data
        weatherData = new WeatherData(28, 65, 15, 12, "Partly Cloudy",
                List.of(
                        new WeatherData.ForecastDay("Wed", 29, "Sunny"),
                        new WeatherData.ForecastDay("Thu", 27, "Cloudy"),
                        new WeatherData.ForecastDay("Fri", 26, "Rain"),
                        new WeatherData.ForecastDay("Sat", 28, "Sunny"),
                        new WeatherData.ForecastDay("Sun", 30, "Sunny")));

        cropRecommendations = List.of(
                new CropRecommendation("Rice", "excellent", "4-5 tons/ha", "120-150 days", "High",
                        List.of(
                                "Loamy soil provides ideal drainage and nutrient retention",
                                "Current pH level (6.8) is optimal for rice cultivation",
                                "Adequate moisture and favorable temperature conditions",
                                "NPK levels support strong vegetative growth")),
                new CropRecommendation("Wheat", "good", "3-4 tons/ha", "110-130 days", "Moderate",
                        List.of(
                                "Soil nutrients support good grain development",
                                "Temperature range suitable for winter wheat",
                                "Moderate water requirement matches current conditions")),
                new CropRecommendation("Maize", "good", "5-6 tons/ha", "90-120 days", "Moderate to High",
                        List.of(
                                "High nitrogen levels promote vegetative growth",
                                "Loamy soil provides good root development",
                                "Current weather conditions favor maize cultivation")));
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
    }

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
            LoginView loginView = new LoginView(email -> {
                DashboardApp newDash = new DashboardApp(stage, email);
                newDash.show();
            });
            Scene scene = new Scene(loginView.getRoot(), 1100, 720);
            scene.getStylesheets().add(MainApp.class.getResource("/styles/app.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
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
                { "soil", "🪨 Soil Data" },
                { "weather", "🌤 Weather" },
                { "recommendations", "💡 Recommendations" },
                { "reports", "📄 Reports" }
        };

        for (String[] tab : tabs) {
            Button btn = new Button(tab[1]);
            btn.getStyleClass().addAll("tab-btn");
            if (tab[0].equals("overview"))
                btn.getStyleClass().add("tab-btn-active");
            btn.setUserData(tab[0]);
            btn.setOnAction(e -> switchTab(tab[0]));
            tabBar.getChildren().add(btn);
        }
        return tabBar;
    }

    private void switchTab(String tabId) {
        // Update active styling
        tabBar.getChildren().forEach(node -> {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("tab-btn-active");
                if (tabId.equals(btn.getUserData()))
                    btn.getStyleClass().add("tab-btn-active");
            }
        });

        // Rebuild content
        VBox contentContainer = (VBox) mainLayout.getCenter();
        if (contentContainer.getChildren().size() > 1)
            contentContainer.getChildren().remove(1);
        contentContainer.getChildren().add(buildContentArea(tabId));
    }

    private javafx.scene.Node buildContentArea(String tabId) {
        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("content-scroll");
        scroll.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        switch (tabId) {
            case "overview" -> {
                VBox overview = new VBox(16);
                overview.setPadding(new Insets(20, 24, 24, 24));
                WeatherView wv = new WeatherView(weatherData);
                HBox advisoryRow = new HBox(16);
                advisoryRow.getChildren().addAll(
                        new IrrigationView(soilData.getMoisture(), weatherData.getRainfall(),
                                weatherData.getTemperature()).getRoot(),
                        new FertilizerView(soilData.getNitrogen(), soilData.getPhosphorus(), soilData.getPotassium(),
                                soilData.getSoilType()).getRoot());
                HBox.setHgrow(advisoryRow.getChildren().get(0), Priority.ALWAYS);
                HBox.setHgrow(advisoryRow.getChildren().get(1), Priority.ALWAYS);
                CropRecommendationsView crv = new CropRecommendationsView(cropRecommendations);
                overview.getChildren().addAll(wv.getRoot(), advisoryRow, crv.getRoot());
                scroll.setContent(overview);
            }
            case "soil" -> {
                VBox soilContainer = new VBox();
                soilContainer.setPadding(new Insets(20, 24, 24, 24));
                soilContainer.setMaxWidth(720);
                SoilDataView sdv = new SoilDataView(data -> {
                    soilData = data;
                    showAlert("✅ Soil data saved! Recommendations updated.");
                });
                soilContainer.getChildren().add(sdv.getRoot());
                scroll.setContent(soilContainer);
            }
            case "weather" -> {
                VBox weatherContainer = new VBox();
                weatherContainer.setPadding(new Insets(20, 24, 24, 24));
                weatherContainer.getChildren().add(new WeatherView(weatherData).getRoot());
                scroll.setContent(weatherContainer);
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
                reco.getChildren().addAll(new CropRecommendationsView(cropRecommendations).getRoot(), advisoryRow);
                scroll.setContent(reco);
            }
            case "reports" -> {
                VBox reportsContainer = new VBox();
                reportsContainer.setPadding(new Insets(20, 24, 24, 24));
                reportsContainer.getChildren().add(new ReportsView().getRoot());
                scroll.setContent(reportsContainer);
            }
        }
        return scroll;
    }

    private void showAlert(String msg) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Smart Crop Advisory");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
