package com.smartcrop.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.function.Consumer;

public class LoginView {

    private final StackPane root;
    private boolean isRegistering = false;

    public LoginView(Consumer<String> onLogin) {
        root = new StackPane();
        root.getStyleClass().add("login-bg");

        VBox card = new VBox(0);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(420);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.TOP_CENTER);

        // Icon
        Label icon = new Label("🌱");
        icon.setStyle("-fx-font-size: 40px; -fx-padding: 10px 20px; -fx-background-color: #16a34a; -fx-background-radius: 50%;");
        VBox.setMargin(icon, new Insets(0, 0, 16, 0));

        Label title = new Label("Smart Crop Advisory System");
        title.getStyleClass().add("login-title");
        title.setTextAlignment(TextAlignment.CENTER);
        title.setWrapText(true);

        Label subtitle = new Label("Welcome back");
        subtitle.getStyleClass().add("login-subtitle");
        VBox.setMargin(subtitle, new Insets(4, 0, 24, 0));

        // Form fields
        VBox nameBox = buildField("Full Name", "text", false);
        TextField nameField = (TextField) ((VBox) nameBox).getChildren().get(1);
        nameBox.setVisible(false);
        nameBox.setManaged(false);

        VBox locationBox = buildField("Farm Location", "text", false);
        TextField locationField = (TextField) ((VBox) locationBox).getChildren().get(1);
        locationBox.setVisible(false);
        locationBox.setManaged(false);

        VBox emailBox = buildField("Email", "email", false);
        TextField emailField = (TextField) ((VBox) emailBox).getChildren().get(1);

        VBox passwordBox = buildField("Password", "password", true);
        PasswordField passwordField = (PasswordField) ((VBox) passwordBox).getChildren().get(1);

        Button submitBtn = new Button("Sign In");
        submitBtn.getStyleClass().add("btn-primary");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(submitBtn, new Insets(16, 0, 0, 0));

        submitBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (!email.isEmpty()) {
                onLogin.accept(email);
            } else {
                showError(card, "Please enter a valid email address.");
            }
        });

        // Toggle register/login
        Hyperlink toggleLink = new Hyperlink("Don't have an account? Register");
        toggleLink.getStyleClass().add("login-toggle-link");
        VBox.setMargin(toggleLink, new Insets(12, 0, 0, 0));

        toggleLink.setOnAction(e -> {
            isRegistering = !isRegistering;
            nameBox.setVisible(isRegistering);
            nameBox.setManaged(isRegistering);
            locationBox.setVisible(isRegistering);
            locationBox.setManaged(isRegistering);
            subtitle.setText(isRegistering ? "Create your account" : "Welcome back");
            submitBtn.setText(isRegistering ? "Create Account" : "Sign In");
            toggleLink.setText(isRegistering ? "Already have an account? Sign in" : "Don't have an account? Register");
        });

        // Demo info box
        VBox demoBox = new VBox(4);
        demoBox.getStyleClass().add("info-box-blue");
        demoBox.setPadding(new Insets(12));
        VBox.setMargin(demoBox, new Insets(16, 0, 0, 0));
        Label demoLabel = new Label("Demo Mode: Enter any email to explore the system.");
        demoLabel.setWrapText(true);
        demoLabel.getStyleClass().add("info-text-blue");
        demoBox.getChildren().add(demoLabel);

        card.getChildren().addAll(icon, title, subtitle, nameBox, locationBox, emailBox, passwordBox,
                submitBtn, toggleLink, demoBox);

        StackPane.setAlignment(card, Pos.CENTER);
        root.getChildren().add(card);
    }

    private VBox buildField(String labelText, String type, boolean isPassword) {
        VBox box = new VBox(6);
        VBox.setMargin(box, new Insets(0, 0, 12, 0));
        Label label = new Label(labelText);
        label.getStyleClass().add("form-label");
        if (isPassword) {
            PasswordField field = new PasswordField();
            field.getStyleClass().add("form-input");
            field.setMaxWidth(Double.MAX_VALUE);
            box.getChildren().addAll(label, field);
        } else {
            TextField field = new TextField();
            field.getStyleClass().add("form-input");
            field.setMaxWidth(Double.MAX_VALUE);
            box.getChildren().addAll(label, field);
        }
        return box;
    }

    private void showError(VBox card, String message) {
        // Remove existing error
        card.getChildren().removeIf(n -> "error-label".equals(n.getUserData()));
        Label err = new Label("⚠ " + message);
        err.setUserData("error-label");
        err.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-padding: 6 0 0 0;");
        card.getChildren().add(err);
    }

    public StackPane getRoot() {
        return root;
    }
}
