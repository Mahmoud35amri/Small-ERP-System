package com.minierp.ui.entreprise;

import com.minierp.controller.EntrepriseController;
import com.minierp.model.Entreprise;
import com.minierp.ui.components.ConfirmDialog;
import com.minierp.ui.components.SearchBar;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

public class EntrepriseView extends BorderPane {
    private TableView<Entreprise> table;
    private EntrepriseController controller;

    public EntrepriseView() {
        this.controller = EntrepriseController.getInstance();
        setPadding(new Insets(20));

        // Top Bar
        HBox topBar = new HBox(10);
        SearchBar searchBar = new SearchBar("Search entreprises...", this::filterTable);
        Button addButton = new Button("New Entreprise");
        addButton.setOnAction(e -> showDialog(null));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> handleDelete());

        topBar.getChildren().addAll(searchBar, addButton, deleteButton);
        setTop(topBar);
        BorderPane.setMargin(topBar, new Insets(0, 0, 10, 0));

        // Table
        table = new TableView<>();
        setupTable();
        setCenter(table);

        refresh();
    }

    private void setupTable() {
        TableColumn<Entreprise, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());

        TableColumn<Entreprise, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNom()));

        TableColumn<Entreprise, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));

        TableColumn<Entreprise, Double> capitalCol = new TableColumn<>("Capital");
        capitalCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getCapital()).asObject());

        table.getColumns().addAll(idCol, nomCol, emailCol, capitalCol);

        table.setRowFactory(tv -> {
            TableRow<Entreprise> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Entreprise rowData = row.getItem();
                    showDialog(rowData);
                }
            });
            return row;
        });
    }

    private void filterTable(String query) {
        if (query == null || query.isEmpty()) {
            refresh();
            return;
        }
        var all = controller.getAll();
        var filtered = all.stream()
                .filter(e -> e.getNom().toLowerCase().contains(query.toLowerCase()))
                .toList();
        table.setItems(FXCollections.observableArrayList(filtered));
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(controller.getAll()));
        table.refresh();
    }

    private void handleDelete() {
        Entreprise selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Notifications.create().text("Select an entreprise to delete").showWarning();
            return;
        }
        if (ConfirmDialog.show("Confirm Delete", "Delete " + selected.getNom() + "?")) {
            controller.delete(selected);
            refresh();
            Notifications.create().text("Deleted successfully").showInformation();
        }
    }

    private void showDialog(Entreprise entreprise) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(entreprise == null ? "New Entreprise" : "Edit Entreprise");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomField = new TextField(entreprise != null ? entreprise.getNom() : "");
        TextField emailField = new TextField(entreprise != null ? entreprise.getEmail() : "");
        TextField capitalField = new TextField(entreprise != null ? String.valueOf(entreprise.getCapital()) : "");

        grid.addRow(0, new Label("Nom:"), nomField);
        grid.addRow(1, new Label("Email:"), emailField);
        grid.addRow(2, new Label("Capital:"), capitalField);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                String nom = nomField.getText();
                String email = emailField.getText();
                double capital = Double.parseDouble(capitalField.getText());

                if (entreprise == null) {
                    Entreprise newEnt = new Entreprise(0, nom, "", "", email, capital);
                    controller.create(newEnt);
                } else {
                    entreprise.setNom(nom);
                    entreprise.setEmail(email);
                    entreprise.setCapital(capital);
                    controller.update(entreprise);
                }
                refresh();
                dialog.close();
                Notifications.create().text("Saved successfully").showInformation();
            } catch (Exception ex) {
                Notifications.create().text("Error: " + ex.getMessage()).showError();
            }
        });

        grid.add(saveBtn, 1, 3);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}