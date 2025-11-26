package com.minierp.ui.utilisateur;

import com.minierp.controller.UtilisateurController;
import com.minierp.model.Utilisateur;
import com.minierp.service.SessionService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UserSettingsWindow {

    public static void show() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("User Settings");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        PasswordField currentPwdField = new PasswordField();
        PasswordField newPwdField = new PasswordField();
        PasswordField confirmPwdField = new PasswordField();

        grid.addRow(0, new Label("Current Password:"), currentPwdField);
        grid.addRow(1, new Label("New Password:"), newPwdField);
        grid.addRow(2, new Label("Confirm Password:"), confirmPwdField);

        Button saveBtn = new Button("Update Password");
        saveBtn.setOnAction(e -> {
            String current = currentPwdField.getText();
            String newVal = newPwdField.getText();
            String confirm = confirmPwdField.getText();

            Utilisateur user = SessionService.getInstance().getConnectedUser();

            if (!user.getPassword().equals(current)) {
                com.minierp.util.DialogHelper.showError("Current password is incorrect");
                return;
            }

            if (!newVal.equals(confirm)) {
                com.minierp.util.DialogHelper.showError("New passwords do not match");
                return;
            }

            if (newVal.isEmpty()) {
                com.minierp.util.DialogHelper.showError("Password cannot be empty");
                return;
            }

            // Update password
            user.setPassword(newVal); // In real app, hash here
            UtilisateurController.getInstance().modifier(user);

            com.minierp.util.DialogHelper.showSuccess("Mot de passe mis à jour.");
            dialog.close();
        });

        grid.add(saveBtn, 1, 3);

        Scene scene = new Scene(grid, 400, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
