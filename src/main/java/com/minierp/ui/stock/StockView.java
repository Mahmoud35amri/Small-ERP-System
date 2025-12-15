package com.minierp.ui.stock;

import com.minierp.controller.ProduitController;
import com.minierp.controller.StockController;
import com.minierp.model.MouvementStock;
import com.minierp.model.Produit;
import com.minierp.model.Stock;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import com.minierp.util.DialogHelper;

import java.util.Optional;

public class StockView extends BorderPane implements com.minierp.ui.Refreshable {

    private final StockController stockController = StockController.getInstance();
    private final ProduitController produitController = ProduitController.getInstance();

    private TableView<Stock> stockTable;
    private TableView<MouvementStock> mouvementTable;

    public StockView() {
        getStylesheets().add(getClass().getResource("/css/stock.css").toExternalForm());
        initializeUI();
        refresh();
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                refresh();
            }
        });
    }

    private void initializeUI() {
        SplitPane splitPane = new SplitPane();

        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));

        Label stockLabel = new Label("Stocks");
        stockLabel.getStyleClass().add("header-label");

        stockTable = new TableView<>();
        setupStockTable();

        HBox stockActions = createStockActions();

        leftPane.getChildren().addAll(stockLabel, stockActions, stockTable);

        // Right Side: Mouvements
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));

        Label mvtLabel = new Label("Mouvements de Stock");
        mvtLabel.getStyleClass().add("header-label");

        mouvementTable = new TableView<>();
        setupMouvementTable();

        rightPane.getChildren().addAll(mvtLabel, mouvementTable);

        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.5);

        setCenter(splitPane);

        // Listener for selection
        stockTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                refreshMouvements(newVal.getProduitId());
            } else {
                mouvementTable.getItems().clear();
            }
        });
    }

    private void setupStockTable() {
        TableColumn<Stock, String> colProduit = new TableColumn<>("Produit");
        colProduit.setCellValueFactory(cellData -> {
            Produit p = produitController.lister().stream()
                    .filter(prod -> prod.getId() == cellData.getValue().getProduitId())
                    .findFirst().orElse(null);
            return new SimpleStringProperty(p != null ? p.getNom() : "Inconnu");
        });

        TableColumn<Stock, Integer> colQte = new TableColumn<>("Quantité");
        colQte.setCellValueFactory(new PropertyValueFactory<>("quantiteActuelle"));

        TableColumn<Stock, Integer> colReserve = new TableColumn<>("Réservé");
        colReserve.setCellValueFactory(new PropertyValueFactory<>("quantiteReservee"));

        stockTable.getColumns().addAll(colProduit, colQte, colReserve);
        @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
        Callback policy = TableView.CONSTRAINED_RESIZE_POLICY;
        stockTable.setColumnResizePolicy(policy);
    }

    private void setupMouvementTable() {
        TableColumn<MouvementStock, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<MouvementStock, Integer> colQte = new TableColumn<>("Quantité");
        colQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        TableColumn<MouvementStock, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));

        mouvementTable.getColumns().addAll(colType, colQte, colDate);
        @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
        Callback policy = TableView.CONSTRAINED_RESIZE_POLICY;
        mouvementTable.setColumnResizePolicy(policy);
    }

    private HBox createStockActions() {
        Button btnAdd = new Button("Ajouter");
        btnAdd.getStyleClass().add("action-button");
        btnAdd.setOnAction(e -> handleStockAction("AJOUT"));

        Button btnRemove = new Button("Retirer");
        btnRemove.getStyleClass().add("secondary-button");
        btnRemove.setOnAction(e -> handleStockAction("RETRAIT"));

        Button btnReserve = new Button("Réserver");
        btnReserve.getStyleClass().add("secondary-button");
        btnReserve.setOnAction(e -> handleStockAction("RESERVATION"));

        Button btnFree = new Button("Libérer");
        btnFree.getStyleClass().add("secondary-button");
        btnFree.setOnAction(e -> handleStockAction("LIBERATION"));

        HBox box = new HBox(10, btnAdd, btnRemove, btnReserve, btnFree);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void handleStockAction(String action) {
        Stock selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogHelper.showWarning("Veuillez sélectionner un produit.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Action Stock: " + action);
        dialog.setHeaderText("Saisir la quantité pour " + action);
        dialog.setContentText("Quantité:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(qtyStr -> {
            try {
                int qty = Integer.parseInt(qtyStr);
                if (qty <= 0)
                    throw new NumberFormatException();

                switch (action) {
                    case "AJOUT":
                        stockController.ajouter(selected.getProduitId(), qty);
                        DialogHelper.showSuccess("Stock ajouté");
                        break;
                    case "RETRAIT":
                        stockController.retirer(selected.getProduitId(), qty);
                        DialogHelper.showSuccess("Stock retiré");
                        break;
                    case "RESERVATION":
                        stockController.reserver(selected.getProduitId(), qty);
                        DialogHelper.showSuccess("Stock réservé");
                        break;
                    case "LIBERATION":
                        stockController.libererReservation(selected.getProduitId(), qty);
                        DialogHelper.showSuccess("Réservation libérée");
                        break;
                }
                refresh();
                refreshMouvements(selected.getProduitId());
            } catch (NumberFormatException e) {
                DialogHelper.showError("Quantité invalide");
            } catch (Exception e) {
                DialogHelper.showError(e.getMessage());
            }
        });
    }

    public void refresh() {
        stockTable.getItems().clear();
        produitController.lister().forEach(p -> {
            stockTable.getItems().add(stockController.getStockByProduit(p.getId()));
        });
    }

    private void refreshMouvements(int produitId) {
        mouvementTable.setItems(FXCollections.observableArrayList(stockController.historiser(produitId)));
    }

}
