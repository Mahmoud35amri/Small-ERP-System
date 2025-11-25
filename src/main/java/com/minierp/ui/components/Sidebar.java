package com.minierp.ui.components;

import com.minierp.service.SessionService;
import com.minierp.ui.ViewManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {
    public Sidebar() {
        getStyleClass().add("sidebar");
        setPrefWidth(250);
        setSpacing(10);

        Label title = new Label("Mini-ERP");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 20 10;");
        getChildren().add(title);

        addNavButton("Entreprise", "Entreprise");

        if (SessionService.getInstance().hasRole("ADMIN")) {
            addNavButton("Utilisateurs", "Utilisateur");
        }
        addNavButton("Categories", "Categorie");
        addNavButton("Produits", "Produit");

        Button settingsBtn = new Button("Settings");
        settingsBtn.getStyleClass().add("sidebar-button");
        settingsBtn.setMaxWidth(Double.MAX_VALUE);
        settingsBtn.setOnAction(e -> com.minierp.ui.utilisateur.UserSettingsWindow.show());
        getChildren().add(settingsBtn);

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("sidebar-button");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> {
            SessionService.getInstance().logout();
            ViewManager.getInstance().showLogin();
        });

        javafx.scene.layout.Region region = new javafx.scene.layout.Region();
        VBox.setVgrow(region, javafx.scene.layout.Priority.ALWAYS);

        getChildren().addAll(region, logoutBtn);
    }

    private void addNavButton(String label, String viewName) {
        Button btn = new Button(label);
        btn.getStyleClass().add("sidebar-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> ViewManager.getInstance().showView(viewName));
        getChildren().add(getChildren().size(), btn);
    }
}
