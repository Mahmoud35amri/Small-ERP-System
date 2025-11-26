package com.minierp.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import java.util.Optional;

public class DialogHelper {

    public static void showSuccess(String message) {
        Notifications.create()
                .title("Succès")
                .text(message)
                .hideAfter(Duration.seconds(3))
                .showInformation();
    }

    public static void showError(String message) {
        Notifications.create()
                .title("Erreur")
                .text(message)
                .hideAfter(Duration.seconds(5))
                .showError();
    }

    public static void showWarning(String message) {
        Notifications.create()
                .title("Attention")
                .text(message)
                .hideAfter(Duration.seconds(3))
                .showWarning();
    }

    public static boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(title);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
