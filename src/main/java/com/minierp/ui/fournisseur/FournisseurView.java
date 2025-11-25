package com.minierp.ui.fournisseur;

import com.minierp.controller.FournisseurController;
import com.minierp.model.Fournisseur;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.Rating;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.util.Optional;

public class FournisseurView extends BorderPane {

    private final FournisseurController fournisseurController = FournisseurController.getInstance();
    private TableView<Fournisseur> table;

    public FournisseurView() {
        getStylesheets().add(getClass().getResource("/css/fournisseur.css").toExternalForm());
        initializeUI();
        refreshTable();
    }

    private void initializeUI() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label header = new Label("Fournisseurs");
        header.getStyleClass().add("header-label");

        HBox toolbar = createToolbar();

        table = new TableView<>();
        setupTable();

        content.getChildren().addAll(header, toolbar, table);
        setCenter(content);
    }

    private HBox createToolbar() {
        Button btnNew = new Button("Nouveau Fournisseur");
        btnNew.getStyleClass().add("action-button");
        btnNew.setOnAction(e -> showEditDialog(null));

        Button btnEdit = new Button("Modifier Fournisseur");
        btnEdit.getStyleClass().add("secondary-button");
        btnEdit.setOnAction(e -> {
            Fournisseur selected = table.getSelectionModel().getSelectedItem();
            if (selected != null)
                showEditDialog(selected);
            else
                showWarning("Veuillez sélectionner un fournisseur.");
        });

        HBox toolbar = new HBox(10, btnNew, btnEdit);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar-container");
        return toolbar;
    }

    private void setupTable() {
        TableColumn<Fournisseur, String> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Fournisseur, String> colNom = new TableColumn<>("Société");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomSociete"));

        TableColumn<Fournisseur, String> colContact = new TableColumn<>("Contact");
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));

        TableColumn<Fournisseur, String> colPhone = new TableColumn<>("Téléphone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Fournisseur, Integer> colEval = new TableColumn<>("Évaluation");
        colEval.setCellValueFactory(new PropertyValueFactory<>("evaluation"));
        colEval.setCellFactory(col -> new TableCell<Fournisseur, Integer>() {
            private final Rating rating = new Rating(5);
            {
                rating.setUpdateOnHover(true);
                rating.setPartialRating(false);
                rating.ratingProperty().addListener((obs, oldVal, newVal) -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        Fournisseur f = getTableRow().getItem();
                        // Avoid infinite loop if update comes from model
                        if (f.getEvaluation() != newVal.intValue()) {
                            fournisseurController.evaluer(f.getId(), newVal.intValue());
                            // No need to refresh whole table, model is updated.
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    rating.setRating(item);
                    setGraphic(rating);
                }
            }
        });

        @SuppressWarnings("unchecked")
        TableColumn<Fournisseur, ?>[] columns = new TableColumn[] { colCode, colNom, colContact, colPhone, colEval };
        table.getColumns().addAll(columns);
        @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
        Callback policy = TableView.CONSTRAINED_RESIZE_POLICY;
        table.setColumnResizePolicy(policy);
    }

    private void showEditDialog(Fournisseur fournisseur) {
        Dialog<Fournisseur> dialog = new Dialog<>();
        dialog.setTitle(fournisseur == null ? "Nouveau Fournisseur" : "Modifier Fournisseur");
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
        TextField contactField = new TextField();
        TextField phoneField = new TextField();
        TextField delaiField = new TextField();
        TextArea conditionsArea = new TextArea();
        conditionsArea.setPrefRowCount(3);

        if (fournisseur != null) {
            codeField.setText(fournisseur.getCode());
            nomField.setText(fournisseur.getNomSociete());
            contactField.setText(fournisseur.getContact());
            phoneField.setText(fournisseur.getPhone());
            delaiField.setText(String.valueOf(fournisseur.getDelaiLivraison()));
            conditionsArea.setText(fournisseur.getConditions());
        }

        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Société:"), 0, 1);
        grid.add(nomField, 1, 1);
        grid.add(new Label("Contact:"), 0, 2);
        grid.add(contactField, 1, 2);
        grid.add(new Label("Téléphone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Délai (jours):"), 0, 4);
        grid.add(delaiField, 1, 4);
        grid.add(new Label("Conditions:"), 0, 5);
        grid.add(conditionsArea, 1, 5);

        Platform.runLater(() -> {
            ValidationSupport validationSupport = new ValidationSupport();
            validationSupport.registerValidator(codeField, Validator.createEmptyValidator("Code requis"));
            validationSupport.registerValidator(nomField, Validator.createEmptyValidator("Nom requis"));
            validationSupport.registerValidator(delaiField,
                    Validator.createRegexValidator("Nombre entier requis", "\\d+", null));
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Fournisseur f = (fournisseur == null) ? new Fournisseur() : fournisseur;
                f.setCode(codeField.getText());
                f.setNomSociete(nomField.getText());
                f.setContact(contactField.getText());
                f.setPhone(phoneField.getText());
                try {
                    f.setDelaiLivraison(Integer.parseInt(delaiField.getText()));
                } catch (NumberFormatException e) {
                    f.setDelaiLivraison(0); // Should be handled by validator
                }
                f.setConditions(conditionsArea.getText());
                if (fournisseur == null)
                    f.setEvaluation(3); // Default rating
                return f;
            }
            return null;
        });

        Optional<Fournisseur> result = dialog.showAndWait();
        result.ifPresent(f -> {
            try {
                if (fournisseur == null) {
                    fournisseurController.creer(f);
                    showSuccess("Fournisseur créé");
                } else {
                    fournisseurController.modifier(f);
                    showSuccess("Fournisseur modifié");
                }
                refreshTable();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });
    }

    public void refreshTable() {
        table.setItems(FXCollections.observableArrayList(fournisseurController.lister()));
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
