package com.minierp.ui.entreprise;

import com.minierp.controller.EntrepriseController;
import com.minierp.model.Entreprise;
import com.minierp.ui.components.SearchBar;
import com.minierp.util.DialogHelper;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;
import com.minierp.model.Facture;
import com.minierp.model.Produit;
import com.minierp.model.Categorie;

public class EntrepriseView extends BorderPane implements com.minierp.ui.Refreshable {
    private TableView<Entreprise> table;
    private EntrepriseController controller;
    private Label lblUsers;
    private Label lblProducts;
    private Label lblCategories;
    private PieChart pieChart;
    private BarChart<String, Number> barChart;

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

        // Main Content ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(10));

        // Table
        table = new TableView<>();
        setupTable();
        table.setPrefHeight(150);

        VBox tableSection = new VBox(5, new Label("Entreprise Details"), table);

        // Dashboard
        lblUsers = new Label("0");
        lblProducts = new Label("0");
        lblCategories = new Label("0");

        HBox dashboard = new HBox(20,
                createStatCard("Utilisateurs", lblUsers),
                createStatCard("Produits", lblProducts),
                createStatCard("Catégories", lblCategories));

        setCenter(scrollPane);

        // Add content to main layout
        mainContent.getChildren().addAll(dashboard, tableSection, createChartsSection());
        scrollPane.setContent(mainContent);

        // Initial load
        refresh();
    }

    private void setupTable() {
        TableColumn<Entreprise, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));

        TableColumn<Entreprise, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nom"));

        TableColumn<Entreprise, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));

        TableColumn<Entreprise, Double> capitalCol = new TableColumn<>("Capital");
        capitalCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("capital"));

        table.getColumns().addAll(idCol, nomCol, emailCol, capitalCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void filterTable(String query) {
        if (query == null || query.isEmpty()) {
            refresh();
            return;
        }

        java.util.List<Entreprise> filtered = controller.lister().stream()
                .filter(e -> e.getNom().toLowerCase().contains(query.toLowerCase()) ||
                        e.getEmail().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        // Also apply multi-tenancy filter if needed, but refresh() handles the source
        // of truth.
        // Better to filter the already filtered list from refresh() logic, or just rely
        // on refresh() logic being base.
        // But here we are filtering all companies.
        // Let's apply the same user filter here.

        com.minierp.model.Utilisateur currentUser = com.minierp.service.SessionService.getInstance().getConnectedUser();
        if (currentUser != null) {
            filtered = filtered.stream()
                    .filter(e -> e.getId() == currentUser.getEntrepriseId())
                    .collect(Collectors.toList());
        }

        table.setItems(FXCollections.observableArrayList(filtered));
    }

    @Override
    public void refresh() {
        java.util.List<Entreprise> list = controller.lister();

        // Filter by current user's enterprise
        com.minierp.model.Utilisateur currentUser = com.minierp.service.SessionService.getInstance().getConnectedUser();
        if (currentUser != null) {
            list = list.stream()
                    .filter(e -> e.getId() == currentUser.getEntrepriseId())
                    .collect(Collectors.toList());
        }

        table.setItems(FXCollections.observableArrayList(list));

        // Update stats
        int totalUsers = list.stream().mapToInt(e -> e.utilisateurs.size()).sum();
        int totalProducts = list.stream().mapToInt(e -> e.produits.size()).sum();
        int totalCategories = list.stream().mapToInt(e -> e.categories.size()).sum();

        if (lblUsers != null)
            lblUsers.setText(String.valueOf(totalUsers));
        if (lblProducts != null)
            lblProducts.setText(String.valueOf(totalProducts));
        if (lblCategories != null)
            lblCategories.setText(String.valueOf(totalCategories));

        if (!list.isEmpty()) {
            updateCharts(list.get(0));
        } else {
            if (pieChart != null)
                pieChart.getData().clear();
            if (barChart != null)
                barChart.getData().clear();
        }
    }

    private VBox createStatCard(String title, Label valueLabel) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: #f4f4f4; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        card.setPrefWidth(200);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private HBox createChartsSection() {
        // Pie Chart
        pieChart = new PieChart();
        pieChart.setTitle("Produits par Catégorie");

        // Bar Chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mois");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenu");

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Revenu Mensuel");

        HBox charts = new HBox(20, pieChart, barChart);
        charts.setPrefHeight(400);
        return charts;
    }

    private void updateCharts(Entreprise current) {
        if (pieChart == null || barChart == null)
            return;

        // Update PieChart
        pieChart.getData().clear();
        Map<Integer, Long> productsByCategory = current.produits.stream()
                .collect(Collectors.groupingBy(Produit::getCategorieId, Collectors.counting()));

        for (Map.Entry<Integer, Long> entry : productsByCategory.entrySet()) {
            String catName = current.categories.stream()
                    .filter(c -> c.getId() == entry.getKey())
                    .map(Categorie::getNom)
                    .findFirst()
                    .orElse("Unknown");
            pieChart.getData().add(new PieChart.Data(catName, entry.getValue()));
        }

        // Update BarChart
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenu");

        Map<String, Double> revenueByMonth = current.factures.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.summingDouble(Facture::getMontant)));

        // Sort by month
        revenueByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                });

        if (!series.getData().isEmpty()) {
            barChart.getData().add(series);
        }
    }

    private void handleDelete() {
        Entreprise selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogHelper.showWarning("Select an entreprise to delete");
            return;
        }
        if (DialogHelper.showConfirmation("Confirm Delete", "Delete " + selected.getNom() + "?")) {
            controller.supprimer(selected);
            refresh();
            DialogHelper.showSuccess("Deleted successfully");
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
                    controller.creer(newEnt);
                } else {
                    entreprise.setNom(nom);
                    entreprise.setEmail(email);
                    entreprise.setCapital(capital);
                    controller.modifier(entreprise);
                }
                refresh();
                dialog.close();
                DialogHelper.showSuccess("Saved successfully");
            } catch (Exception ex) {
                DialogHelper.showError("Error: " + ex.getMessage());
            }
        });

        grid.add(saveBtn, 1, 3);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}