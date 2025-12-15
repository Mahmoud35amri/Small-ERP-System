package com.minierp.util;

import com.minierp.controller.CategorieController;
import com.minierp.controller.ClientController;
import com.minierp.controller.CommandeController;
import com.minierp.controller.EntrepriseController;
import com.minierp.controller.ProduitController;
import com.minierp.controller.StockController;
import com.minierp.controller.UtilisateurController;
import com.minierp.model.Categorie;
import com.minierp.model.Client;
import com.minierp.model.Commande;
import com.minierp.model.Entreprise;
import com.minierp.model.LigneCommande;
import com.minierp.model.Produit;
import com.minierp.model.Utilisateur;
import com.minierp.service.EntrepriseRegistry;
import java.time.LocalDate;

public class TestDataLoader {
    public static void load() {
        try {
            System.out.println("Loading Tunisian specific test data...");

            // 1. Create Entreprises
            Entreprise e1 = new Entreprise(0, "Tunisie Électronique", "Les Berges du Lac 2, Tunis", "71 123 456",
                    "contact@techexpert.tn", 150000.0);
            Entreprise e2 = new Entreprise(0, "Sfax Agro-Alimentaire", "Route de Gabès Km 4, Sfax", "74 987 654",
                    "export@sfaxagro.tn", 300000.0);

            EntrepriseController.getInstance().creer(e1);
            EntrepriseController.getInstance().creer(e2);

            // ==========================================
            // DATA FOR: Tunisie Électronique
            // ==========================================
            EntrepriseRegistry.getInstance().switchTo(e1.getId());

            // Users
            Utilisateur user1 = new Utilisateur(0, "Mohamed", "Ben Ali", "mohamed@techexpert.tn", "123456", "GERANT",
                    true);
            user1.setEntrepriseId(e1.getId());
            UtilisateurController.getInstance().creer(user1);

            // Categories
            Categorie catInfo = CategorieController.getInstance().creer(new Categorie(0, "Informatique", null));
            Categorie catLaptop = CategorieController.getInstance()
                    .creer(new Categorie(0, "Ordinateurs Portables", catInfo.getId()));
            Categorie catPhone = CategorieController.getInstance().creer(new Categorie(0, "Téléphonie", null));
            Categorie catAccessoire = CategorieController.getInstance()
                    .creer(new Categorie(0, "Accessoires", catInfo.getId()));

            // Products
            // Constructor: id, ref, nom, catId, stock, prixVente, prixPromo, enPromo,
            // promoPct, bestSeller
            Produit p1 = new Produit(0, "REF-001", "PC Portable Dell Latitude", catLaptop.getId(), 15, 2500.0, 0.0,
                    false, 0.0, true);
            Produit p2 = new Produit(0, "REF-002", "iPhone 15 Pro Max", catPhone.getId(), 8, 5200.0, 0.0, false, 0.0,
                    true);
            Produit p3 = new Produit(0, "REF-003", "Souris Logitech Sans Fil", catAccessoire.getId(), 50, 85.0, 0.0,
                    false, 0.0, false);
            Produit p4 = new Produit(0, "REF-004", "Clavier Mécanique Gamer", catAccessoire.getId(), 20, 180.0, 150.0,
                    true, 15.0, false);

            p1 = ProduitController.getInstance().creer(p1);
            p2 = ProduitController.getInstance().creer(p2);
            p3 = ProduitController.getInstance().creer(p3);
            p4 = ProduitController.getInstance().creer(p4);

            // Initialize Stock
            StockController.getInstance().initialiserStock(p1.getId(), 15);
            StockController.getInstance().initialiserStock(p2.getId(), 8);
            StockController.getInstance().initialiserStock(p3.getId(), 50);
            StockController.getInstance().initialiserStock(p4.getId(), 20);

            // Clients
            // Constructor: id, code, nom, prenom, email, phone, adresse, active, date
            Client c1 = new Client(0, "CL-001", "Société Tunisienne de Banque", "STB", "achat@stb.com.tn", "71 000 000",
                    "Rue Hédi Nouira, Tunis", true, LocalDate.now().minusMonths(6));
            Client c2 = new Client(0, "CL-002", "Groupe Loukil", "Si Walid", "w.loukil@groupe.tn", "55 555 555",
                    "Charguia 1", true, LocalDate.now().minusMonths(2));
            Client c3 = new Client(0, "CL-003", "Ben Salah Ahmed", "Ahmed", "ahmed.bs@gmail.com", "98 123 123",
                    "Ariana", true, LocalDate.now().minusDays(10));

            c1 = ClientController.getInstance().creer(c1);
            c2 = ClientController.getInstance().creer(c2);
            c3 = ClientController.getInstance().creer(c3);

            // Orders
            // Order 1: Recent, Validated (STB bought Laptops)
            Commande cmd1 = new Commande();
            cmd1.setClientId(c1.getId());
            cmd1.setDate(LocalDate.now()); // Set date strictly
            cmd1.setStatus("BROUILLON"); // Explicit status
            cmd1 = CommandeController.getInstance().creer(cmd1);

            LigneCommande l1 = new LigneCommande();
            l1.setProduitId(p1.getId());
            l1.setQuantite(5);
            l1.setPrixUnitaire(p1.getPrixVente());
            // Total calculation handled by controller/DAO logic
            CommandeController.getInstance().ajouterLigne(cmd1.getId(), l1);

            cmd1.setDate(LocalDate.now().minusDays(2));
            CommandeController.getInstance().valider(cmd1.getId());

            // Order 2: Pending (Ahmed wants a phone)
            Commande cmd2 = new Commande();
            cmd2.setClientId(c3.getId());
            cmd2.setDate(LocalDate.now());
            cmd2.setStatus("BROUILLON");
            cmd2 = CommandeController.getInstance().creer(cmd2);

            LigneCommande l2 = new LigneCommande();
            l2.setProduitId(p2.getId());
            l2.setQuantite(1);
            l2.setPrixUnitaire(p2.getPrixVente());
            CommandeController.getInstance().ajouterLigne(cmd2.getId(), l2);
            // Remains BROUILLON

            // ==========================================
            // DATA FOR: Sfax Agro-Alimentaire
            // ==========================================
            EntrepriseRegistry.getInstance().switchTo(e2.getId());

            // Users
            Utilisateur user2 = new Utilisateur(0, "Fatma", "Karray", "fatma@sfaxagro.tn", "123456", "ADMIN", true);
            user2.setEntrepriseId(e2.getId());
            UtilisateurController.getInstance().creer(user2);

            // Categories
            Categorie catHuile = CategorieController.getInstance().creer(new Categorie(0, "Huiles", null));
            Categorie catFruits = CategorieController.getInstance()
                    .creer(new Categorie(0, "Fruits Secs & Dattes", null));

            // Products
            Produit pAgro1 = new Produit(0, "HUI-5L", "Bidon Huile d'Olive Vierge 5L", catHuile.getId(), 200, 140.0,
                    0.0, false, 0.0, true);
            Produit pAgro2 = new Produit(0, "DAT-1KG", "Dattes Deglet Nour (Régime) 1kg", catFruits.getId(), 500,
                    14.500, 12.000, true, 17.0, true);

            pAgro1 = ProduitController.getInstance().creer(pAgro1);
            pAgro2 = ProduitController.getInstance().creer(pAgro2);

            StockController.getInstance().initialiserStock(pAgro1.getId(), 200);
            StockController.getInstance().initialiserStock(pAgro2.getId(), 500);

            System.out.println("Tunisian test data loaded successfully.");

        } catch (Exception e) {
            System.err.println("Error loading test data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
