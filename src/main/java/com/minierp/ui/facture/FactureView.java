package com.minierp.ui.facture;

import com.minierp.controller.FactureController;
import com.minierp.model.Facture;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.time.format.DateTimeFormatter;

public class FactureView extends BorderPane implements com.minierp.ui.Refreshable {

    private final FactureController factureController = FactureController.getInstance();
    private TableView<Facture> table;

    public FactureView() {
        getStylesheets().add(getClass().getResource("/css/facture.css").toExternalForm());
        initializeUI();
        refresh();
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                refreshTable();
            }
        });
    }

    // ...

    @Override
    public void refresh() {
        refreshTable();
    }

    private void initializeUI() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label header = new Label("Factures");
        header.getStyleClass().add("header-label");

        HBox toolbar = createToolbar();

        table = new TableView<>();
        setupTable();

        content.getChildren().addAll(header, toolbar, table);
        setCenter(content);
    }

    private HBox createToolbar() {
        Button btnPay = new Button("Marquer Payée");
        btnPay.getStyleClass().add("action-button");
        btnPay.setOnAction(e -> handleMarkPaid());

        Button btnPrint = new Button("Imprimer PDF");
        btnPrint.getStyleClass().add("secondary-button");
        btnPrint.setOnAction(e -> showSuccess("Impression simulée (PDF généré)"));

        HBox toolbar = new HBox(10, btnPay, btnPrint);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar-container");
        return toolbar;
    }

    private void setupTable() {
        TableColumn<Facture, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Facture, Integer> colCmdId = new TableColumn<>("Commande ID");
        colCmdId.setCellValueFactory(new PropertyValueFactory<>("commandeId"));

        TableColumn<Facture, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));

        TableColumn<Facture, String> colMontant = new TableColumn<>("Montant");
        colMontant.setCellValueFactory(cellData -> new SimpleStringProperty(
                String.format("%.2f €", cellData.getValue().getMontant())));

        TableColumn<Facture, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new TableCell<Facture, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    getStyleClass().removeAll("status-payee", "status-non-payee");
                    if ("PAYEE".equals(item)) {
                        getStyleClass().add("status-payee");
                    } else {
                        getStyleClass().add("status-non-payee");
                    }
                }
            }
        });

        table.getColumns().addAll(colId, colCmdId, colDate, colMontant, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void handleMarkPaid() {
        Facture selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner une facture.");
            return;
        }

        try {
            factureController.marquerPayee(selected.getId());
            showSuccess("Facture marquée comme payée");
            refreshTable();
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    public void refreshTable() {
        table.setItems(FXCollections.observableArrayList(factureController.lister()));
        table.refresh();
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
