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

        Label titleLabel = new Label("Connexion");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setText("mohamed@techexpert.tn");

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setText("123456");

        Button loginButton = new Button("Se connecter");
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
                    .title("Bienvenue")
                    .text("Connexion réussie")
                    .showInformation();
        } catch (Exception e) {
            errorLabel.setText("Identifiants incorrects");
            errorLabel.setVisible(true);
            Notifications.create()
                    .title("Échec de connexion")
                    .text(e.getMessage())
                    .showError();
        }
    }
}
