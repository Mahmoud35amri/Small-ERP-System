package com.minierp.ui.categorie;

import com.minierp.controller.CategorieController;
import com.minierp.controller.ProduitController;
import com.minierp.model.Categorie;
import com.minierp.model.Produit;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import com.minierp.util.DialogHelper;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CategorieView extends BorderPane implements com.minierp.ui.Refreshable {

    private final CategorieController categorieController = CategorieController.getInstance();
    private final ProduitController produitController = ProduitController.getInstance();

    private TreeView<Categorie> treeView;
    private TableView<Produit> produitsTable;
    private TreeItem<Categorie> rootItem;

    public CategorieView() {
        getStylesheets().add(getClass().getResource("/css/categorie.css").toExternalForm());
        initializeUI();
        refresh();
    }

    @Override
    public void refresh() {
        refreshTree();
    }

    private void initializeUI() {
        // Toolbar
        Button btnAdd = new Button("Nouvelle Catégorie");
        btnAdd.getStyleClass().add("action-button");
        btnAdd.setOnAction(e -> showEditDialog(null));

        Button btnEdit = new Button("Modifier");
        btnEdit.getStyleClass().add("action-button");
        btnEdit.setOnAction(e -> {
            TreeItem<Categorie> selected = treeView.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getValue() != null) {
                showEditDialog(selected.getValue());
            } else {
                DialogHelper.showWarning("Veuillez sélectionner une catégorie à modifier.");
            }
        });

        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().add("delete-button");
        btnDelete.setOnAction(e -> deleteSelectedCategorie());

        HBox toolbar = new HBox(10, btnAdd, btnEdit, btnDelete);
        toolbar.getStyleClass().add("toolbar-container");
        toolbar.setAlignment(Pos.CENTER_LEFT);
        setTop(toolbar);

        // SplitPane
        SplitPane splitPane = new SplitPane();
        splitPane.getStyleClass().add("split-pane");

        // Left: TreeView
        treeView = new TreeView<>();
        treeView.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<Categorie> call(TreeView<Categorie> param) {
                return new DragDropTreeCell();
            }
        });
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue() != null) {
                refreshProducts(newVal.getValue().getId());
            } else {
                produitsTable.getItems().clear();
            }
        });

        VBox leftPane = new VBox(new Label("Arborescence"), treeView);
        leftPane.setSpacing(5);
        VBox.setVgrow(treeView, Priority.ALWAYS);

        // Right: TableView
        produitsTable = new TableView<>();
        setupProduitsTable();

        VBox rightPane = new VBox(new Label("Produits de la catégorie"), produitsTable);
        rightPane.setSpacing(5);
        VBox.setVgrow(produitsTable, Priority.ALWAYS);

        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.3);
        setCenter(splitPane);
    }

    private void setupProduitsTable() {
        TableColumn<Produit, String> colRef = new TableColumn<>("Référence");
        colRef.setCellValueFactory(new PropertyValueFactory<>("ref"));

        TableColumn<Produit, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Produit, Double> colPrix = new TableColumn<>("Prix Vente");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixVente"));

        TableColumn<Produit, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));

        produitsTable.getColumns().addAll(colRef, colNom, colPrix, colStock);
        produitsTable.setPlaceholder(new Label("Aucun produit dans cette catégorie"));
    }

    public void refreshTree() {
        List<Categorie> allCategories = categorieController.lister();

        // Find roots (categories with no parent or parentId 0/null)
        Categorie dummyRoot = new Categorie(); // Hidden root
        rootItem = new TreeItem<>(dummyRoot);
        treeView.setShowRoot(false);

        // Build hierarchy
        buildTree(rootItem, allCategories);
        treeView.setRoot(rootItem);
    }

    private void buildTree(TreeItem<Categorie> parentItem, List<Categorie> allCategories) {
        Integer parentId = parentItem.getValue().getId() == 0 ? null : parentItem.getValue().getId();

        // If parentItem is the dummy root, we look for categories with null parentId
        if (parentItem.getValue() == rootItem.getValue()) {
            List<Categorie> roots = allCategories.stream()
                    .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                    .collect(Collectors.toList());

            for (Categorie cat : roots) {
                TreeItem<Categorie> item = new TreeItem<>(cat);
                parentItem.getChildren().add(item);
                buildTree(item, allCategories);
            }
        } else {
            List<Categorie> children = allCategories.stream()
                    .filter(c -> Objects.equals(c.getParentId(), parentId))
                    .collect(Collectors.toList());

            for (Categorie cat : children) {
                TreeItem<Categorie> item = new TreeItem<>(cat);
                parentItem.getChildren().add(item);
                buildTree(item, allCategories);
            }
        }
        parentItem.setExpanded(true);
    }

    public void refreshProducts(int categorieId) {
        List<Produit> allProduits = produitController.lister();
        List<Produit> filtered = allProduits.stream()
                .filter(p -> p.getCategorieId() == categorieId)
                .collect(Collectors.toList());
        produitsTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void deleteSelectedCategorie() {
        TreeItem<Categorie> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getValue() == null) {
            DialogHelper.showWarning("Aucune catégorie sélectionnée.");
            return;
        }

        // Check if has children
        if (!selected.getChildren().isEmpty()) {
            DialogHelper.showError("Impossible de supprimer une catégorie contenant des sous-catégories.");
            return;
        }

        // Check if has products
        List<Produit> products = produitController.lister().stream()
                .filter(p -> p.getCategorieId() == selected.getValue().getId())
                .collect(Collectors.toList());

        if (!products.isEmpty()) {
            DialogHelper.showError("Impossible de supprimer une catégorie contenant des produits.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la catégorie ?");
        alert.setContentText("Voulez-vous vraiment supprimer " + selected.getValue().getNom() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categorieController.supprimer(selected.getValue().getId());
                DialogHelper.showSuccess("Catégorie supprimée avec succès");
                refreshTree();
            } catch (Exception ex) {
                DialogHelper.showError("Erreur lors de la suppression: " + ex.getMessage());
            }
        }
    }

    private void showEditDialog(Categorie categorie) {
        Dialog<Categorie> dialog = new Dialog<>();
        dialog.setTitle(categorie == null ? "Nouvelle Catégorie" : "Modifier Catégorie");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        if (categorie != null)
            nomField.setText(categorie.getNom());

        ComboBox<Categorie> parentCombo = new ComboBox<>();
        List<Categorie> allCats = categorieController.lister();
        // Remove self and children to avoid cycles if editing
        if (categorie != null) {
            allCats = allCats.stream()
                    .filter(c -> c.getId() != categorie.getId()) // Not self
                    .collect(Collectors.toList());
        }
        parentCombo.getItems().addAll(allCats);

        // Set selected parent
        if (categorie != null && categorie.getParentId() != null) {
            allCats.stream()
                    .filter(c -> c.getId() == categorie.getParentId())
                    .findFirst()
                    .ifPresent(parentCombo::setValue);
        }

        // Custom cell factory for ComboBox to show names
        Callback<ListView<Categorie>, ListCell<Categorie>> cellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        };
        parentCombo.setCellFactory(cellFactory);
        parentCombo.setButtonCell(cellFactory.call(null));

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Parent:"), 0, 1);
        grid.add(parentCombo, 1, 1);

        ValidationSupport validationSupport = new ValidationSupport();
        validationSupport.registerValidator(nomField, Validator.createEmptyValidator("Le nom est requis"));

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Categorie c = (categorie == null) ? new Categorie() : categorie;
                c.setNom(nomField.getText());
                c.setParentId(parentCombo.getValue() != null ? parentCombo.getValue().getId() : null);
                return c;
            }
            return null;
        });

        Optional<Categorie> result = dialog.showAndWait();

        result.ifPresent(c -> {
            try {
                if (categorie == null) {
                    categorieController.creer(c);
                    DialogHelper.showSuccess("Catégorie créée avec succès");
                } else {
                    categorieController.modifier(c);
                    DialogHelper.showSuccess("Catégorie modifiée avec succès");
                }
                refreshTree();
            } catch (Exception ex) {
                DialogHelper.showError("Erreur: " + ex.getMessage());
            }
        });
    }

    // Inner class for Drag & Drop
    private class DragDropTreeCell extends TreeCell<Categorie> {
        public DragDropTreeCell() {
            setOnDragDetected(event -> {
                if (getItem() == null)
                    return;
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(getItem().getId()));
                db.setContent(content);
                event.consume();
            });

            setOnDragOver(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    int draggedId = Integer.parseInt(db.getString());
                    Categorie targetParent = getItem();
                    Integer newParentId = (targetParent == null) ? null : targetParent.getId();

                    if (draggedId != (newParentId == null ? -1 : newParentId)) {
                        try {
                            categorieController.deplacerCategorie(draggedId, newParentId);
                            refreshTree();
                            success = true;
                            DialogHelper.showSuccess("Catégorie déplacée");
                        } catch (Exception ex) {
                            DialogHelper.showError("Erreur lors du déplacement: " + ex.getMessage());
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }

        @Override
        protected void updateItem(Categorie item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.getNom());
            }
        }
    }
}
