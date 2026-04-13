package com.smartcrop.view;

import com.smartcrop.service.SupabaseService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.util.function.Consumer;

public class LoginView {

    private final StackPane root;
    private boolean isRegistering = false;
    private final SupabaseService supabase = SupabaseService.getInstance();

    public LoginView(Consumer<String> onLogin) {
        root = new StackPane();
        root.getStyleClass().add("login-bg");

        VBox card = new VBox(0);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(420);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.TOP_CENTER);

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

        // Register-only fields
        VBox nameBox = buildTextField("Full Name");
        nameBox.setVisible(false);
        nameBox.setManaged(false);

        VBox locationBox = buildTextField("Farm Location");
        locationBox.setVisible(false);
        locationBox.setManaged(false);

        // Always-visible fields
        VBox emailBox = buildTextField("Email");
        TextField emailField = (TextField) emailBox.getChildren().get(1);

        VBox passwordBox = buildPasswordField("Password");
        PasswordField passwordField = (PasswordField) passwordBox.getChildren().get(1);

        // Status label (loading / error)
        Label statusLabel = new Label("");
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-font-size: 12px; -fx-padding: 4 0 0 0;");
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        Button submitBtn = new Button("Sign In");
        submitBtn.getStyleClass().add("btn-primary");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(submitBtn, new Insets(16, 0, 0, 0));

        submitBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showStatus(statusLabel, "⚠ Please enter your email and password.", false);
                return;
            }

            submitBtn.setDisable(true);
            submitBtn.setText(isRegistering ? "Creating account…" : "Signing in…");
            showStatus(statusLabel, "Connecting to server…", null);

            new Thread(() -> {
                try {
                    String loggedInEmail = isRegistering
                            ? supabase.signUp(email, password)
                            : supabase.signIn(email, password);

                    Platform.runLater(() -> {
                        submitBtn.setDisable(false);
                        submitBtn.setText(isRegistering ? "Create Account" : "Sign In");
                        statusLabel.setVisible(false);
                        statusLabel.setManaged(false);
                        onLogin.accept(loggedInEmail);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        submitBtn.setDisable(false);
                        submitBtn.setText(isRegistering ? "Create Account" : "Sign In");
                        showStatus(statusLabel, "❌ " + ex.getMessage(), false);
                    });
                }
            }).start();
        });

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
            toggleLink.setText(isRegistering
                    ? "Already have an account? Sign in"
                    : "Don't have an account? Register");
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);
        });

        card.getChildren().addAll(icon, title, subtitle,
                nameBox, locationBox, emailBox, passwordBox,
                statusLabel, submitBtn, toggleLink);

        StackPane.setAlignment(card, Pos.CENTER);
        root.getChildren().add(card);
    }

    private VBox buildTextField(String labelText) {
        VBox box = new VBox(6);
        VBox.setMargin(box, new Insets(0, 0, 12, 0));
        Label label = new Label(labelText);
        label.getStyleClass().add("form-label");
        TextField field = new TextField();
        field.getStyleClass().add("form-input");
        field.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(label, field);
        return box;
    }

    private VBox buildPasswordField(String labelText) {
        VBox box = new VBox(6);
        VBox.setMargin(box, new Insets(0, 0, 12, 0));
        Label label = new Label(labelText);
        label.getStyleClass().add("form-label");
        PasswordField field = new PasswordField();
        field.getStyleClass().add("form-input");
        field.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(label, field);
        return box;
    }

    private void showStatus(Label label, String message, Boolean isError) {
        label.setText(message);
        if (isError == null) {
            label.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");
        } else if (isError) {
            label.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");
        } else {
            label.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");
        }
        label.setVisible(true);
        label.setManaged(true);
    }

    public StackPane getRoot() { return root; }
}
