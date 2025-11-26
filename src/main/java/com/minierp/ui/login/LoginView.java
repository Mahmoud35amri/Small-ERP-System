package com.minierp.ui.login;

import com.minierp.service.SessionService;
import com.minierp.ui.ViewManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Notifications;

public class LoginView extends VBox {
    private TextField emailField;
    private PasswordField passwordField;
    private Label errorLabel;

    public LoginView() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setMaxWidth(400);
        setStyle(
                "-fx-padding: 40; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 5;");

        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setText("admin@techsolutions.com"); // Pre-fill for convenience

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setText("admin123"); // Pre-fill

        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> handleLogin());

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);

        getChildren().addAll(titleLabel, emailField, passwordField, loginButton, errorLabel);
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            SessionService.getInstance().login(email, password);
            ViewManager.getInstance().showMainInterface();
            Notifications.create()
                    .title("Welcome")
                    .text("Logged in successfully")
                    .showInformation();
        } catch (Exception e) {
            errorLabel.setText("Invalid credentials");
            errorLabel.setVisible(true);
            Notifications.create()
                    .title("Login Failed")
                    .text(e.getMessage())
                    .showError();
        }
    }
}
