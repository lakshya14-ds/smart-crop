package com.smartcrop;

import com.smartcrop.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Smart Crop Advisory System");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);

        LoginView loginView = new LoginView(email -> {
            DashboardApp dashApp = new DashboardApp(primaryStage, email);
            dashApp.show();
        });

        Scene scene = new Scene(loginView.getRoot(), 1100, 720);
        scene.getStylesheets().add(MainApp.class.getResource("/styles/app.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
