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

import com.minierp.util.DialogHelper;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.util.Optional;

public class FournisseurView extends BorderPane implements com.minierp.ui.Refreshable {

    private final FournisseurController fournisseurController = FournisseurController.getInstance();
    private TableView<Fournisseur> table;

    public FournisseurView() {
        getStylesheets().add(getClass().getResource("/css/fournisseur.css").toExternalForm());
        initializeUI();
        refresh();
    }

    // ...

    @Override
    public void refresh() {
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
                DialogHelper.showWarning("Veuillez sélectionner un fournisseur.");
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
        TextField evaluationField = new TextField(); // Simple text field for evaluation
        TextArea conditionsArea = new TextArea();
        conditionsArea.setPrefRowCount(3);

        if (fournisseur != null) {
            codeField.setText(fournisseur.getCode());
            nomField.setText(fournisseur.getNomSociete());
            contactField.setText(fournisseur.getContact());
            phoneField.setText(fournisseur.getPhone());
            delaiField.setText(String.valueOf(fournisseur.getDelaiLivraison()));
            evaluationField.setText(String.valueOf(fournisseur.getEvaluation()));
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
        grid.add(new Label("Évaluation (0-5):"), 0, 5);
        grid.add(evaluationField, 1, 5);
        grid.add(new Label("Conditions:"), 0, 6);
        grid.add(conditionsArea, 1, 6);

        Platform.runLater(() -> {
            ValidationSupport validationSupport = new ValidationSupport();
            validationSupport.registerValidator(codeField, Validator.createEmptyValidator("Code requis"));
            validationSupport.registerValidator(nomField, Validator.createEmptyValidator("Nom requis"));
            validationSupport.registerValidator(delaiField,
                    Validator.createRegexValidator("Nombre entier requis", "\\d+", null));
            validationSupport.registerValidator(evaluationField,
                    Validator.createRegexValidator("0-5 requis", "[0-5]", null));
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
                try {
                    f.setEvaluation(Integer.parseInt(evaluationField.getText()));
                } catch (NumberFormatException e) {
                    f.setEvaluation(3); // Default if empty or invalid
                }
                f.setConditions(conditionsArea.getText());
                // removed default evaluation setting as it's handled above or by input
                return f;
            }
            return null;
        });

        Optional<Fournisseur> result = dialog.showAndWait();
        result.ifPresent(f -> {
            try {
                if (fournisseur == null) {
                    fournisseurController.creer(f);
                    DialogHelper.showSuccess("Fournisseur créé");
                } else {
                    fournisseurController.modifier(f);
                    DialogHelper.showSuccess("Fournisseur modifié");
                }
                refreshTable();
            } catch (Exception ex) {
                DialogHelper.showError(ex.getMessage());
            }
        });
    }

    public void refreshTable() {
        table.setItems(FXCollections.observableArrayList(fournisseurController.lister()));
    }

}
