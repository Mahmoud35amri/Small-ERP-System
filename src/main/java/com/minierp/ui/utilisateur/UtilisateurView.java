package com.minierp.ui.utilisateur;

import com.minierp.controller.UtilisateurController;
import com.minierp.model.Utilisateur;
import com.minierp.ui.components.ConfirmDialog;
import com.minierp.ui.components.SearchBar;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.time.LocalDateTime;

public class UtilisateurView extends BorderPane implements com.minierp.ui.Refreshable {
    private TableView<Utilisateur> table;
    private UtilisateurController controller;

    public UtilisateurView() {
        this.controller = UtilisateurController.getInstance();
        setPadding(new Insets(20));

        // Top Bar
        HBox topBar = new HBox(10);
        SearchBar searchBar = new SearchBar("Search users...", this::filterTable);

        Button addButton = new Button("New User");
        addButton.setOnAction(e -> showDialog(null));

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Utilisateur selected = table.getSelectionModel().getSelectedItem();
            if (selected != null)
                showDialog(selected);
            else
                Notifications.create().text("Select a user to edit").showWarning();
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> handleDelete());

        Button resetPwdButton = new Button("Reset Password");
        resetPwdButton.setOnAction(e -> handleResetPassword());

        Button lockButton = new Button("Lock/Unlock");
        lockButton.setOnAction(e -> handleToggleLock());

        topBar.getChildren().addAll(searchBar, addButton, editButton, deleteButton, resetPwdButton, lockButton);
        setTop(topBar);
        BorderPane.setMargin(topBar, new Insets(0, 0, 10, 0));

        // Table
        table = new TableView<>();
        table.setEditable(true);
        setupTable();
        setCenter(table);

        refresh();
    }

    private void setupTable() {
        TableColumn<Utilisateur, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());

        TableColumn<Utilisateur, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNom()));
        nomCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nomCol.setOnEditCommit(e -> {
            e.getRowValue().setNom(e.getNewValue());
            controller.update(e.getRowValue());
        });

        TableColumn<Utilisateur, String> prenomCol = new TableColumn<>("Prenom");
        prenomCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPrenom()));
        prenomCol.setCellFactory(TextFieldTableCell.forTableColumn());
        prenomCol.setOnEditCommit(e -> {
            e.getRowValue().setPrenom(e.getNewValue());
            controller.update(e.getRowValue());
        });

        TableColumn<Utilisateur, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setOnEditCommit(e -> {
            e.getRowValue().setEmail(e.getNewValue());
            controller.update(e.getRowValue());
        });

        TableColumn<Utilisateur, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole()));

        TableColumn<Utilisateur, Boolean> actifCol = new TableColumn<>("Actif");
        actifCol.setCellValueFactory(cell -> new SimpleBooleanProperty(cell.getValue().isActif()));

        TableColumn<Utilisateur, LocalDateTime> lastLoginCol = new TableColumn<>("Dernier Connexion");
        lastLoginCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDernierConnexion()));

        table.getColumns().addAll(idCol, nomCol, prenomCol, emailCol, roleCol, actifCol, lastLoginCol);
    }

    private void filterTable(String query) {
        if (query == null || query.isEmpty()) {
            refresh();
            return;
        }
        var all = controller.getAll();
        var filtered = all.stream()
                .filter(u -> u.getNom().toLowerCase().contains(query.toLowerCase()) ||
                        u.getEmail().toLowerCase().contains(query.toLowerCase()))
                .toList();
        table.setItems(FXCollections.observableArrayList(filtered));
        table.refresh();
    }

    @Override
    public void refresh() {
        table.setItems(FXCollections.observableArrayList(controller.getAll()));
        table.refresh();
    }

    private void handleDelete() {
        Utilisateur selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Notifications.create().text("Select a user to delete").showWarning();
            return;
        }
        if (ConfirmDialog.show("Confirm Delete", "Delete " + selected.getNom() + "?")) {
            controller.delete(selected);
            refresh();
            Notifications.create().text("Deleted successfully").showInformation();
        }
    }

    private void handleResetPassword() {
        Utilisateur selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Notifications.create().text("Select a user").showWarning();
            return;
        }
        if (ConfirmDialog.show("Reset Password", "Reset password for " + selected.getNom() + " to '123456'?")) {
            controller.resetPassword(selected);
            Notifications.create().text("Password reset successfully").showInformation();
        }
    }

    private void handleToggleLock() {
        Utilisateur selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Notifications.create().text("Select a user").showWarning();
            return;
        }
        controller.toggleLock(selected);
        refresh();
        Notifications.create().text("User status updated").showInformation();
    }

    private void showDialog(Utilisateur user) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(user == null ? "New User" : "Edit User");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomField = new TextField(user != null ? user.getNom() : "");
        TextField prenomField = new TextField(user != null ? user.getPrenom() : "");
        TextField emailField = new TextField(user != null ? user.getEmail() : "");
        ComboBox<String> roleBox = new ComboBox<>(FXCollections.observableArrayList("ADMIN", "GERANT", "EMPLOYE"));
        roleBox.setValue(user != null ? user.getRole() : "EMPLOYE");
        CheckBox actifBox = new CheckBox("Actif");
        actifBox.setSelected(user != null ? user.isActif() : true);

        // Validation
        ValidationSupport validationSupport = new ValidationSupport();
        validationSupport.registerValidator(nomField, Validator.createEmptyValidator("Nom is required"));
        validationSupport.registerValidator(emailField, Validator.createEmptyValidator("Email is required"));

        grid.addRow(0, new Label("Nom:"), nomField);
        grid.addRow(1, new Label("Prenom:"), prenomField);
        grid.addRow(2, new Label("Email:"), emailField);
        grid.addRow(3, new Label("Role:"), roleBox);
        grid.addRow(4, new Label("Actif:"), actifBox);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            if (validationSupport.isInvalid()) {
                Notifications.create().text("Please fix validation errors").showError();
                return;
            }
            try {
                String nom = nomField.getText();
                String prenom = prenomField.getText();
                String email = emailField.getText();
                String role = roleBox.getValue();
                boolean actif = actifBox.isSelected();

                // ... inside showDialog method ...
                String tempPassword = "123456";
                if (user == null) {
                    Utilisateur newUser = new Utilisateur(0, nom, prenom, email, tempPassword, role, actif);
                    controller.create(newUser);
                } else {
                    user.setNom(nom);
                    user.setPrenom(prenom);
                    user.setEmail(email);
                    user.setRole(role);
                    user.setActif(actif);
                    controller.update(user);
                }
                refresh();
                dialog.close();
                Notifications.create().text("User Created with password: " + tempPassword).showInformation();
            } catch (Exception ex) {
                Notifications.create().text("Error: " + ex.getMessage()).showError();
            }
        });

        grid.add(saveBtn, 1, 5);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
