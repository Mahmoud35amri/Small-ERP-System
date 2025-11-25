package com.minierp.ui.commande;

import com.minierp.controller.ClientController;
import com.minierp.controller.CommandeController;
import com.minierp.controller.FactureController;
import com.minierp.controller.ProduitController;
import com.minierp.model.Client;
import com.minierp.model.Commande;
import com.minierp.model.LigneCommande;
import com.minierp.model.Produit;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class CommandeView extends BorderPane {

    private final CommandeController commandeController = CommandeController.getInstance();
    private final ClientController clientController = ClientController.getInstance();
    private final ProduitController produitController = ProduitController.getInstance();
    private final FactureController factureController = FactureController.getInstance();

    private TableView<Commande> table;

    public CommandeView() {
        getStylesheets().add(getClass().getResource("/css/commande.css").toExternalForm());
        initializeUI();
        refreshTable();
    }

    private void initializeUI() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label header = new Label("Commandes");
        header.getStyleClass().add("header-label");

        HBox toolbar = createToolbar();

        table = new TableView<>();
        setupTable();

        content.getChildren().addAll(header, toolbar, table);
        setCenter(content);
    }

    private HBox createToolbar() {
        Button btnNew = new Button("Nouvelle Commande");
        btnNew.getStyleClass().add("action-button");
        btnNew.setOnAction(e -> showCreateDialog());

        Button btnValidate = new Button("Valider");
        btnValidate.getStyleClass().add("secondary-button");
        btnValidate.setOnAction(e -> handleStatusChange("VALIDEE"));

        Button btnCancel = new Button("Annuler");
        btnCancel.getStyleClass().add("secondary-button");
        btnCancel.setOnAction(e -> handleStatusChange("ANNULEE"));

        Button btnDeliver = new Button("Livrer");
        btnDeliver.getStyleClass().add("secondary-button");
        btnDeliver.setOnAction(e -> handleStatusChange("LIVREE"));

        Button btnFacture = new Button("Générer Facture");
        btnFacture.getStyleClass().add("secondary-button");
        btnFacture.setOnAction(e -> handleGenerateFacture());

        HBox toolbar = new HBox(10, btnNew, btnValidate, btnCancel, btnDeliver, btnFacture);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar-container");
        return toolbar;
    }

    private void setupTable() {
        TableColumn<Commande, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Commande, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));

        TableColumn<Commande, String> colClient = new TableColumn<>("Client");
        colClient.setCellValueFactory(cellData -> {
            Client c = clientController.lister().stream()
                    .filter(cl -> cl.getId() == cellData.getValue().getClientId())
                    .findFirst().orElse(null);
            return new SimpleStringProperty(c != null ? c.getNom() + " " + c.getPrenom() : "Inconnu");
        });

        TableColumn<Commande, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Commande, String> colTotal = new TableColumn<>("Total TTC");
        colTotal.setCellValueFactory(cellData -> {
            double total = commandeController.calculerTTC(cellData.getValue().getId());
            return new SimpleStringProperty(String.format("%.2f €", total));
        });

        @SuppressWarnings("unchecked")
        TableColumn<Commande, ?>[] columns = new TableColumn[] { colId, colDate, colClient, colStatus, colTotal };
        table.getColumns().addAll(columns);
        @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
        Callback policy = TableView.CONSTRAINED_RESIZE_POLICY;
        table.setColumnResizePolicy(policy);

        // Double click to show details
        table.setRowFactory(tv -> {
            TableRow<Commande> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showDetailsDialog(row.getItem());
                }
            });
            return row;
        });
    }

    private void showCreateDialog() {
        Dialog<Commande> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Commande");
        dialog.setHeaderText("Créer une commande");
        dialog.initModality(Modality.APPLICATION_MODAL);

        if (this.getScene() != null) {
            dialog.initOwner(this.getScene().getWindow());
        }

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Client> clientCombo = new ComboBox<>();
        clientCombo.setItems(FXCollections.observableArrayList(clientController.lister()));
        clientCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom() + " " + item.getPrenom());
            }
        });
        clientCombo.setButtonCell(clientCombo.getCellFactory().call(null));

        // Line Items Table
        TableView<LigneCommande> linesTable = new TableView<>();
        ObservableList<LigneCommande> lines = FXCollections.observableArrayList();
        linesTable.setItems(lines);
        linesTable.setPrefHeight(200);

        TableColumn<LigneCommande, String> colProd = new TableColumn<>("Produit");
        colProd.setCellValueFactory(cellData -> {
            Produit p = produitController.lister().stream()
                    .filter(prod -> prod.getId() == cellData.getValue().getProduitId())
                    .findFirst().orElse(null);
            return new SimpleStringProperty(p != null ? p.getNom() : "Inconnu");
        });

        TableColumn<LigneCommande, Integer> colQty = new TableColumn<>("Qté");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        TableColumn<LigneCommande, Double> colPrice = new TableColumn<>("Prix U.");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));

        TableColumn<LigneCommande, Double> colTotalLine = new TableColumn<>("Total HT");
        colTotalLine.setCellValueFactory(new PropertyValueFactory<>("totalHT"));

        @SuppressWarnings("unchecked")
        TableColumn<LigneCommande, ?>[] columns = new TableColumn[] { colProd, colQty, colPrice, colTotalLine };
        linesTable.getColumns().addAll(columns);
        @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
        Callback policy = TableView.CONSTRAINED_RESIZE_POLICY;
        linesTable.setColumnResizePolicy(policy);

        // Add Line Form
        ComboBox<Produit> productCombo = new ComboBox<>();
        productCombo.setItems(FXCollections.observableArrayList(produitController.lister()));
        productCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom() + " (" + item.getPrixVente() + "€)");
            }
        });
        productCombo.setButtonCell(productCombo.getCellFactory().call(null));

        TextField qtyField = new TextField("1");
        qtyField.setPrefWidth(60);

        Button btnAddLine = new Button("Ajouter");
        btnAddLine.setOnAction(e -> {
            Produit p = productCombo.getValue();
            if (p != null) {
                try {
                    int qty = Integer.parseInt(qtyField.getText());
                    if (qty > 0) {
                        LigneCommande line = new LigneCommande();
                        line.setProduitId(p.getId());
                        line.setQuantite(qty);
                        line.setPrixUnitaire(p.getPrixVente());
                        line.setTotalHT(qty * p.getPrixVente());
                        line.setTotalTTC(line.getTotalHT() * 1.20);
                        lines.add(line);
                    }
                } catch (NumberFormatException ex) {
                    // Ignore invalid qty
                }
            }
        });

        HBox addLineBox = new HBox(10, new Label("Produit:"), productCombo, new Label("Qté:"), qtyField, btnAddLine);
        addLineBox.setAlignment(Pos.CENTER_LEFT);

        grid.add(new Label("Client:"), 0, 0);
        grid.add(clientCombo, 1, 0);
        grid.add(linesTable, 0, 1, 2, 1);
        grid.add(addLineBox, 0, 2, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                if (clientCombo.getValue() != null && !lines.isEmpty()) {
                    Commande c = new Commande();
                    c.setClientId(clientCombo.getValue().getId());
                    c.setLignes(new ArrayList<>(lines));
                    return c;
                }
            }
            return null;
        });

        Optional<Commande> result = dialog.showAndWait();
        result.ifPresent(c -> {
            try {
                commandeController.creer(c);
                // Recalculate totals for lines as controller might expect them set or re-set
                // Actually controller's ajouterLigne does calculation, but we set list
                // directly.
                // We should ensure totals are correct. The logic above sets them.
                // However, CommandeController.creer just adds to list.
                // We might need to ensure stock reservation isn't done here (it's done on
                // validation).
                showSuccess("Commande créée");
                refreshTable();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void handleStatusChange(String newStatus) {
        Commande selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner une commande.");
            return;
        }

        try {
            switch (newStatus) {
                case "VALIDEE":
                    commandeController.valider(selected.getId());
                    showSuccess("Commande validée");
                    break;
                case "ANNULEE":
                    commandeController.annuler(selected.getId());
                    showSuccess("Commande annulée");
                    break;
                case "LIVREE":
                    commandeController.livrer(selected.getId());
                    showSuccess("Commande livrée");
                    break;
            }
            refreshTable();
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    private void handleGenerateFacture() {
        Commande selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner une commande.");
            return;
        }

        if (!"VALIDEE".equals(selected.getStatus()) && !"LIVREE".equals(selected.getStatus())) {
            showWarning("La commande doit être validée ou livrée pour être facturée.");
            return;
        }

        try {
            factureController.genererDepuisCommande(selected.getId());
            showSuccess("Facture générée avec succès");
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    private void showDetailsDialog(Commande c) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails Commande #" + c.getId());
        dialog.setHeaderText("Détails de la commande");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        if (this.getScene() != null) {
            dialog.initOwner(this.getScene().getWindow());
        }

        TableView<LigneCommande> linesTable = new TableView<>();
        linesTable.setItems(FXCollections.observableArrayList(c.getLignes()));

        TableColumn<LigneCommande, String> colProd = new TableColumn<>("Produit");
        colProd.setCellValueFactory(cellData -> {
            Produit p = produitController.lister().stream()
                    .filter(prod -> prod.getId() == cellData.getValue().getProduitId())
                    .findFirst().orElse(null);
            return new SimpleStringProperty(p != null ? p.getNom() : "Inconnu");
        });

        TableColumn<LigneCommande, Integer> colQty = new TableColumn<>("Qté");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        TableColumn<LigneCommande, Double> colPrice = new TableColumn<>("Prix U.");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));

        TableColumn<LigneCommande, Double> colTotal = new TableColumn<>("Total TTC");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalTTC"));

        @SuppressWarnings("unchecked")
        TableColumn<LigneCommande, ?>[] columns = new TableColumn[] { colProd, colQty, colPrice, colTotal };
        linesTable.getColumns().addAll(columns);
        @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
        Callback policy = TableView.CONSTRAINED_RESIZE_POLICY;
        linesTable.setColumnResizePolicy(policy);

        VBox content = new VBox(10, linesTable);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait();
    }

    public void refreshTable() {
        table.setItems(FXCollections.observableArrayList(commandeController.lister()));
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
