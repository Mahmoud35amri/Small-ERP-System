package com.minierp.ui.produit;

import com.minierp.controller.CategorieController;
import com.minierp.controller.ProduitController;
import com.minierp.model.Categorie;
import com.minierp.model.Produit;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.application.Platform;
import org.controlsfx.control.Notifications;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.util.List;
import java.util.Optional;

public class ProduitView extends BorderPane {

    private final ProduitController produitController = ProduitController.getInstance();
    private final CategorieController categorieController = CategorieController.getInstance();

    private TableView<Produit> table;
    private FilteredList<Produit> filteredData;
    private TextField searchField;
    private ComboBox<Categorie> categoryFilter;

    public ProduitView() {
        getStylesheets().add(getClass().getResource("/css/produit.css").toExternalForm());
        initializeUI();
        refreshTable();
    }

    private void initializeUI() {
        // Toolbar
        HBox toolbar = createToolbar();
        setTop(toolbar);

        // Table
        table = new TableView<>();
        setupTable();
        setCenter(table);

        // Padding
        setPadding(new Insets(10));
    }

    private HBox createToolbar() {
        // Search
        searchField = new TextField();
        searchField.setPromptText("Rechercher (Réf, Nom)...");
        searchField.getStyleClass().add("search-field");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable());

        // Category Filter
        categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Toutes les catégories");
        categoryFilter.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Toutes les catégories" : item.getNom());
            }
        });
        categoryFilter.setButtonCell(categoryFilter.getCellFactory().call(null));
        categoryFilter.getItems().add(null); // Option for "All"
        categoryFilter.getItems().addAll(categorieController.lister());
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());

        // Actions
        Button btnAdd = new Button("Nouveau Produit");
        btnAdd.getStyleClass().add("action-button");
        btnAdd.setOnAction(e -> showEditDialog(null));

        Button btnStock = new Button("Ajuster Stock");
        btnStock.getStyleClass().add("secondary-button");
        btnStock.setOnAction(e -> {
            Produit selected = table.getSelectionModel().getSelectedItem();
            if (selected != null)
                showStockDialog(selected);
            else
                showWarning("Veuillez sélectionner un produit.");
        });

        Button btnPromo = new Button("Promotion");
        btnPromo.getStyleClass().add("secondary-button");
        btnPromo.setOnAction(e -> {
            Produit selected = table.getSelectionModel().getSelectedItem();
            if (selected != null)
                showPromoDialog(selected);
            else
                showWarning("Veuillez sélectionner un produit.");
        });

        Button btnRefresh = new Button("Rafraîchir");
        btnRefresh.getStyleClass().add("secondary-button");
        btnRefresh.setOnAction(e -> refreshTable());

        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().add("secondary-button");
        btnDelete.setOnAction(e -> deleteSelectedProduit());

        HBox toolbar = new HBox(15, searchField, categoryFilter, btnAdd, btnStock, btnPromo, btnDelete, btnRefresh);
        toolbar.getStyleClass().add("toolbar-container");
        toolbar.setAlignment(Pos.CENTER_LEFT);
        return toolbar;
    }

    private void setupTable() {
        table.setEditable(true);

        TableColumn<Produit, String> colRef = new TableColumn<>("Réf");
        colRef.setCellValueFactory(new PropertyValueFactory<>("ref"));
        colRef.setPrefWidth(100);

        TableColumn<Produit, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(200);

        TableColumn<Produit, String> colCat = new TableColumn<>("Catégorie");
        colCat.setCellValueFactory(cellData -> {
            int catId = cellData.getValue().getCategorieId();
            return new SimpleObjectProperty<>(getCategoryName(catId));
        });
        colCat.setPrefWidth(150);

        TableColumn<Produit, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colStock.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colStock.setOnEditCommit(e -> {
            Produit p = e.getRowValue();
            if (e.getNewValue() >= 0) {
                p.setQuantiteStock(e.getNewValue());
                produitController.modifier(p);
                showSuccess("Stock mis à jour");
            } else {
                refreshTable(); // Revert
                showError("Le stock ne peut pas être négatif");
            }
        });
        colStock.setPrefWidth(100);

        TableColumn<Produit, Double> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        colPrix.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colPrix.setOnEditCommit(e -> {
            Produit p = e.getRowValue();
            if (e.getNewValue() >= 0) {
                p.setPrixVente(e.getNewValue());
                produitController.modifier(p);
                showSuccess("Prix mis à jour");
            } else {
                refreshTable(); // Revert
                showError("Le prix ne peut pas être négatif");
            }
        });
        colPrix.setPrefWidth(100);

        TableColumn<Produit, Void> colBadges = new TableColumn<>("Status");
        colBadges.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Produit, Void> call(TableColumn<Produit, Void> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Produit p = getTableView().getItems().get(getIndex());
                            HBox badges = new HBox(5);
                            badges.setAlignment(Pos.CENTER_LEFT);

                            if (p.isEnPromotion()) {
                                Label promo = new Label("PROMO -" + (int) p.getPromotionPourcentage() + "%");
                                promo.getStyleClass().add("badge-promo");
                                badges.getChildren().add(promo);
                            }

                            if (p.isBestSeller()) {
                                Label best = new Label("BEST");
                                best.getStyleClass().add("badge-bestseller");
                                badges.getChildren().add(best);
                            }
                            setGraphic(badges);
                        }
                    }
                };
            }
        });
        colBadges.setPrefWidth(150);

        table.getColumns().addAll(colRef, colNom, colCat, colStock, colPrix, colBadges);
    }

    private String getCategoryName(int id) {
        return categorieController.lister().stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .map(Categorie::getNom)
                .orElse("Inconnue");
    }

    public void refreshTable() {
        List<Produit> produits = produitController.lister();
        filteredData = new FilteredList<>(FXCollections.observableArrayList(produits), p -> true);
        table.setItems(filteredData);
        filterTable();

        // Refresh categories in filter
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add(null);
        categoryFilter.getItems().addAll(categorieController.lister());
    }

    private void filterTable() {
        if (filteredData == null)
            return;

        String search = searchField.getText().toLowerCase();
        Categorie cat = categoryFilter.getValue();

        filteredData.setPredicate(p -> {
            boolean matchesSearch = search.isEmpty() ||
                    p.getNom().toLowerCase().contains(search) ||
                    p.getRef().toLowerCase().contains(search);

            boolean matchesCat = cat == null || p.getCategorieId() == cat.getId();

            return matchesSearch && matchesCat;
        });
    }

    private void showEditDialog(Produit produit) {
        try {
            Dialog<Produit> dialog = new Dialog<>();
            dialog.setTitle(produit == null ? "Nouveau Produit" : "Modifier Produit");
            dialog.setHeaderText(null);

            if (this.getScene() != null) {
                dialog.initOwner(this.getScene().getWindow());
            }

            ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField refField = new TextField();
            TextField nomField = new TextField();
            TextField stockField = new TextField();
            TextField prixField = new TextField();
            CheckBox bestSellerCheck = new CheckBox("Best Seller");

            ComboBox<Categorie> catCombo = new ComboBox<>();
            catCombo.getItems().addAll(categorieController.lister());
            catCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Categorie item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getNom());
                }
            });
            catCombo.setButtonCell(catCombo.getCellFactory().call(null));

            if (produit != null) {
                refField.setText(produit.getRef());
                nomField.setText(produit.getNom());
                stockField.setText(String.valueOf(produit.getQuantiteStock()));
                prixField.setText(String.valueOf(produit.getPrixVente()));
                bestSellerCheck.setSelected(produit.isBestSeller());

                categorieController.lister().stream()
                        .filter(c -> c.getId() == produit.getCategorieId())
                        .findFirst()
                        .ifPresent(catCombo::setValue);
            }

            grid.add(new Label("Référence:"), 0, 0);
            grid.add(refField, 1, 0);
            grid.add(new Label("Nom:"), 0, 1);
            grid.add(nomField, 1, 1);
            grid.add(new Label("Catégorie:"), 0, 2);
            grid.add(catCombo, 1, 2);
            grid.add(new Label("Stock:"), 0, 3);
            grid.add(stockField, 1, 3);
            grid.add(new Label("Prix Vente:"), 0, 4);
            grid.add(prixField, 1, 4);
            grid.add(bestSellerCheck, 1, 5);

            Platform.runLater(() -> {
                ValidationSupport validationSupport = new ValidationSupport();
                validationSupport.registerValidator(refField,
                        Validator.createEmptyValidator("Référence requise"));
                validationSupport.registerValidator(nomField,
                        Validator.createEmptyValidator("Nom requis"));
                validationSupport.registerValidator(stockField,
                        Validator.createRegexValidator("Nombre entier requis", "\\d+", null));
                validationSupport.registerValidator(prixField,
                        Validator.createRegexValidator("Nombre valide requis", "\\d*\\.?\\d+",
                                null));
            });

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    Produit p = (produit == null) ? new Produit() : produit;
                    p.setRef(refField.getText());
                    p.setNom(nomField.getText());
                    if (catCombo.getValue() != null) {
                        p.setCategorieId(catCombo.getValue().getId());
                    }
                    try {
                        p.setQuantiteStock(Integer.parseInt(stockField.getText()));
                        p.setPrixVente(Double.parseDouble(prixField.getText()));
                    } catch (NumberFormatException e) {
                        // Should be handled by validator, but safe fallback
                    }
                    p.setBestSeller(bestSellerCheck.isSelected());
                    return p;
                }
                return null;
            });

            Optional<Produit> result = dialog.showAndWait();
            result.ifPresent(p -> {
                try {
                    if (produit == null) {
                        produitController.creer(p);
                        showSuccess("Produit créé");
                    } else {
                        produitController.modifier(p);
                        showSuccess("Produit modifié");
                    }
                    refreshTable();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture du dialogue: " + e.getMessage());
        }
    }

    private void showStockDialog(Produit produit) {
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Ajuster Stock");
        dialog.setHeaderText("Ajustement pour: " + produit.getNom());
        dialog.setContentText("Quantité à ajouter (négatif pour retirer):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(qty -> {
            try {
                int delta = Integer.parseInt(qty);
                produitController.ajusterStock(produit.getId(), delta);
                refreshTable();
                showSuccess("Stock ajusté");
            } catch (NumberFormatException e) {
                showError("Nombre invalide");
            } catch (Exception e) {
                showError(e.getMessage());
            }
        });
    }

    private void showPromoDialog(Produit produit) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Promotion");
        dialog.setHeaderText("Appliquer une promotion pour: " + produit.getNom());

        ButtonType applyType = new ButtonType("Appliquer", ButtonBar.ButtonData.OK_DONE);
        ButtonType removeType = new ButtonType("Annuler Promo", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(applyType, removeType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField percentField = new TextField();
        percentField.setPromptText("%");
        if (produit.isEnPromotion()) {
            percentField.setText(String.valueOf(produit.getPromotionPourcentage()));
        }

        grid.add(new Label("Pourcentage (%):"), 0, 0);
        grid.add(percentField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyType) {
                try {
                    return Double.parseDouble(percentField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            } else if (dialogButton == removeType) {
                return -1.0; // Signal to remove
            }
            return null;
        });

        Optional<Double> result = dialog.showAndWait();
        result.ifPresent(val -> {
            try {
                if (val == -1.0) {
                    produitController.annulerPromotion(produit.getId());
                    showSuccess("Promotion annulée");
                } else if (val != null) {
                    produitController.appliquerPromotion(produit.getId(), val);
                    showSuccess("Promotion appliquée");
                } else {
                    showError("Pourcentage invalide");
                }
                refreshTable();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        });
    }

    private void deleteSelectedProduit() {
        Produit selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner un produit à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le produit ?");
        alert.setContentText("Voulez-vous vraiment supprimer " + selected.getNom() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                produitController.supprimer(selected.getId());
                showSuccess("Produit supprimé avec succès");
                refreshTable();
            } catch (Exception ex) {
                showError("Erreur lors de la suppression: " + ex.getMessage());
            }
        }
    }

    private void showSuccess(String message) {
        Notifications.create().title("Succès").text(message).showInformation();
    }

    private void showError(String message) {
        Notifications.create().title("Erreur").text(message).showError();
    }

    private void showWarning(String message) {
        Notifications.create().title("Attention").text(message).showWarning();
    }
}
