package com.minierp.ui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import java.util.HashMap;
import java.util.Map;

public class ViewManager {
    private static ViewManager instance;
    private BorderPane mainLayout;
    private StackPane contentArea;
    private Map<String, Node> views = new HashMap<>();

    private ViewManager() {
        mainLayout = new BorderPane();
        contentArea = new StackPane();
        mainLayout.setCenter(contentArea);
    }

    public static synchronized ViewManager getInstance() {
        if (instance == null) {
            instance = new ViewManager();
        }
        return instance;
    }

    public BorderPane getMainLayout() {
        return mainLayout;
    }

    public void setView(String viewName, Node node) {
        views.put(viewName, node);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(node);
    }

    public void showView(String viewName) {
        if (views.containsKey(viewName)) {
            Node view = views.get(viewName);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            if (view instanceof Refreshable) {
                ((Refreshable) view).refresh();
            }
        }
    }

    public void showMainInterface() {
        mainLayout.setLeft(new com.minierp.ui.components.Sidebar());
        // Default view
        showView("Entreprise");
    }

    public void showLogin() {
        mainLayout.setLeft(null);
        showView("Login");
    }
}
