package com.minierp;

import com.minierp.ui.ViewManager;
import com.minierp.ui.login.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Load Test Data
        com.minierp.util.TestDataLoader.load();

        ViewManager viewManager = ViewManager.getInstance();

        // Register Views
        viewManager.setView("Login", new LoginView());
        viewManager.setView("Entreprise", new com.minierp.ui.entreprise.EntrepriseView());
        viewManager.setView("Utilisateur", new com.minierp.ui.utilisateur.UtilisateurView());
        viewManager.setView("Categorie", new com.minierp.ui.categorie.CategorieView());
        viewManager.setView("Produit", new com.minierp.ui.produit.ProduitView());
        viewManager.setView("Stock", new com.minierp.ui.stock.StockView());
        viewManager.setView("client", new com.minierp.ui.client.ClientView());
        viewManager.setView("fournisseur", new com.minierp.ui.fournisseur.FournisseurView());
        viewManager.setView("Commande", new com.minierp.ui.commande.CommandeView());
        viewManager.setView("Facture", new com.minierp.ui.facture.FactureView());
        viewManager.setView("AIAssistant", new com.minierp.ui.ai.AIAssistantView());

        viewManager.showLogin();

        Scene scene = new Scene(viewManager.getMainLayout(), 1024, 768);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

        stage.setTitle("Mini-ERP");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
