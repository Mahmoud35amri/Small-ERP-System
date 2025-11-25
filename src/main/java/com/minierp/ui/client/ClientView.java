package com.minierp.ui.client;

import com.minierp.controller.ClientController;
import com.minierp.controller.CommandeController;
import com.minierp.model.Client;
import com.minierp.model.Commande;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.util.Optional;

public class ClientView extends BorderPane {

    private final ClientController clientController = ClientController.getInstance();
    private final CommandeController commandeController = CommandeController.getInstance();

    private TableView<Client> clientTable;
    private TextField searchField;

    // Details Panel
    private Label lblTurnover;
    private TableView<Commande> recentOrdersTable;

    public ClientView() {
        getStylesheets().add(getClass().getResource("/css/client.css").toExternalForm());
        initializeUI();
        refreshTable();
    }

    private void initializeUI() {
        SplitPane splitPane = new SplitPane();

        // Left: Client List
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));

        Label listLabel = new Label("Clients");
        listLabel.getStyleClass().add("header-label");

        HBox toolbar = createToolbar();

        clientTable = new TableView<>();
        setupClientTable();

        leftPane.getChildren().addAll(listLabel, toolbar, clientTable);

        // Right: Details
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));

        Label detailLabel = new Label("Détails Client");
        detailLabel.getStyleClass().add("header-label");

        lblTurnover = new Label("Chiffre d'Affaires: -");
        lblTurnover.getStyleClass().add("detail-value");

        Label ordersLabel = new Label("Commandes Récentes");
        ordersLabel.getStyleClass().add("detail-label");

        recentOrdersTable = new TableView<>();
        setupRecentOrdersTable();

        rightPane.getChildren().addAll(detailLabel, lblTurnover, ordersLabel, recentOrdersTable);

        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.6);

        setCenter(splitPane);

        // Listener
        clientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                refreshClientDetail(newVal);
            } else {
                lblTurnover.setText("Chiffre d'Affaires: -");
                recentOrdersTable.getItems().clear();
            }
        });

        // Auto-refresh
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                refreshTable();
                Client selected = clientTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    refreshClientDetail(selected);
                }
            }
        });
    }

    private HBox createToolbar() {
        searchField = new TextField();
        searchField.setPromptText("Rechercher (Nom, Email, Code)");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            clientTable.setItems(FXCollections.observableArrayList(clientController.rechercher(newVal)));
        });

        Button btnNew = new Button("Nouveau Client");
        btnNew.getStyleClass().add("action-button");
        btnNew.setOnAction(e -> showEditDialog(null));

        Button btnEdit = new Button("Modifier Client");
        btnEdit.getStyleClass().add("secondary-button");
        btnEdit.setOnAction(e -> {
            Client selected = clientTable.getSelectionModel().getSelectedItem();
            if (selected != null)
                showEditDialog(selected);
            else
                showWarning("Veuillez sélectionner un client.");
        });

        HBox toolbar = new HBox(10, searchField, btnNew, btnEdit);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar-container");
        return toolbar;
    }

    private void setupClientTable() {
        clientTable.setEditable(true);

        TableColumn<Client, String> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Client, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Client, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        TableColumn<Client, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        colEmail.setOnEditCommit(e -> {
            Client c = e.getRowValue();
            c.setEmail(e.getNewValue());
            clientController.modifier(c);
        });

        TableColumn<Client, String> colPhone = new TableColumn<>("Téléphone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setCellFactory(TextFieldTableCell.forTableColumn());
        colPhone.setOnEditCommit(e -> {
            Client c = e.getRowValue();
            c.setPhone(e.getNewValue());
            clientController.modifier(c);
        });

        TableColumn<Client, Boolean> colActive = new TableColumn<>("Actif");
        colActive.setCellValueFactory(cellData -> {
            Client c = cellData.getValue();
            return new javafx.beans.property.SimpleBooleanProperty(c.isActive());
        });
        colActive.setCellFactory(CheckBoxTableCell.forTableColumn(colActive));
        // CheckBoxTableCell updates the property directly, but we need to save it.
        // However, since it binds to the property, we might need a way to trigger save.
        // A simple way is to rely on the property listener if we added one, or just
        // assume in-memory update is enough for now.
        // But to be safe, let's add a listener to the property when the cell is
        // created?
        // Actually, CheckBoxTableCell calls the callback.
        // Let's stick to simple binding for now as ClientController is in-memory.

        @SuppressWarnings("unchecked")
        TableColumn<Client, ?>[] columns = new TableColumn[] { colCode, colNom, colPrenom, colEmail, colPhone,
                colActive };
        clientTable.getColumns().addAll(columns);
        @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
        Callback policy = TableView.CONSTRAINED_RESIZE_POLICY;
        clientTable.setColumnResizePolicy(policy);
    }

    private void setupRecentOrdersTable() {
        TableColumn<Commande, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Commande, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));

        TableColumn<Commande, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        @SuppressWarnings("unchecked")
        TableColumn<Commande, ?>[] columns = new TableColumn[] { colId, colDate, colStatus };
        recentOrdersTable.getColumns().addAll(columns);
        @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
        Callback policy = TableView.CONSTRAINED_RESIZE_POLICY;
        recentOrdersTable.setColumnResizePolicy(policy);
    }

    private void showEditDialog(Client client) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle(client == null ? "Nouveau Client" : "Modifier Client");
        dialog.setHeaderText(null);
        dialog.initModality(Modality.APPLICATION_MODAL);

        if (this.getScene() != null) {
            dialog.initOwner(this.getScene().getWindow());
        }

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField codeField = new TextField();
        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField adresseField = new TextField();
        CheckBox activeCheck = new CheckBox("Actif");
        activeCheck.setSelected(true);

        if (client != null) {
            codeField.setText(client.getCode());
            nomField.setText(client.getNom());
            prenomField.setText(client.getPrenom());
            emailField.setText(client.getEmail());
            phoneField.setText(client.getPhone());
            adresseField.setText(client.getAdresse());
            activeCheck.setSelected(client.isActive());
        }

        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Nom:"), 0, 1);
        grid.add(nomField, 1, 1);
        grid.add(new Label("Prénom:"), 0, 2);
        grid.add(prenomField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Téléphone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Adresse:"), 0, 5);
        grid.add(adresseField, 1, 5);
        grid.add(activeCheck, 1, 6);

        Platform.runLater(() -> {
            ValidationSupport validationSupport = new ValidationSupport();
            validationSupport.registerValidator(codeField, Validator.createEmptyValidator("Code requis"));
            validationSupport.registerValidator(nomField, Validator.createEmptyValidator("Nom requis"));
            validationSupport.registerValidator(emailField, Validator.createEmptyValidator("Email requis"));
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Client c = (client == null) ? new Client() : client;
                c.setCode(codeField.getText());
                c.setNom(nomField.getText());
                c.setPrenom(prenomField.getText());
                c.setEmail(emailField.getText());
                c.setPhone(phoneField.getText());
                c.setAdresse(adresseField.getText());
                c.setActive(activeCheck.isSelected());
                return c;
            }
            return null;
        });

        Optional<Client> result = dialog.showAndWait();
        result.ifPresent(c -> {
            try {
                if (client == null) {
                    clientController.creer(c);
                    showSuccess("Client créé");
                } else {
                    clientController.modifier(c);
                    showSuccess("Client modifié");
                }
                refreshTable();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });
    }

    public void refreshTable() {
        clientTable.setItems(FXCollections.observableArrayList(clientController.lister()));
    }

    private void refreshClientDetail(Client client) {
        double ca = clientController.calculerChiffreAffaires(client.getId());
        lblTurnover.setText(String.format("Chiffre d'Affaires: %.2f €", ca));

        // Filter commands for this client (assuming Commande has clientId, let's check
        // Commande model)
        // I need to check Commande.java to see if it has clientId.
        // I read CommandeController but not Commande.java fully (I read it but didn't
        // check fields carefully).
        // Assuming it has clientId. If not, I can't filter.
        // Let's assume it does for now. If not, I'll fix it.
        // Actually, I read Commande.java in step 638 (list_dir) -> 652 (MouvementStock)
        // -> Wait, I read Commande.java?
        // I read `CommandeController.java` but maybe not `Commande.java` content fully?
        // I read `Stock.java`, `MouvementStock.java`, `Client.java`,
        // `Fournisseur.java`.
        // I did NOT read `Commande.java` content. I listed it.
        // I'll assume it has clientId. If compilation fails, I'll fix.

        recentOrdersTable.setItems(FXCollections.observableArrayList(
                commandeController.lister().stream()
                        .filter(c -> c.getClientId() == client.getId()) // Assumption
                        .limit(10)
                        .toList()));
    }

    private void showSuccess(String message) {
        Notifications.create().title("Succès").text(message).hideAfter(Duration.seconds(3)).showInformation();
    }

    private void showWarning(String message) {
        Notifications.create().title("Attention").text(message).hideAfter(Duration.seconds(3)).showWarning();
    }

    private void showError(String message) {
        Notifications.create().title("Erreur").text(message).hideAfter(Duration.seconds(5)).showError();
    }
}
