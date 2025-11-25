package com.minierp.util;

import com.minierp.controller.EntrepriseController;
import com.minierp.controller.UtilisateurController;
import com.minierp.model.Entreprise;
import com.minierp.model.Utilisateur;

public class TestDataLoader {
    public static void load() {
        // Users
        Utilisateur admin = new Utilisateur(1, "Admin", "User", "admin@minierp.com", "admin123", "ADMIN", true);
        Utilisateur gerant = new Utilisateur(2, "Gerant", "User", "gerant@minierp.com", "gerant123", "GERANT", true);

        UtilisateurController.getInstance().create(admin);
        UtilisateurController.getInstance().create(gerant);

        // Entreprises
        Entreprise e1 = new Entreprise(0, "Tech Solutions", "123 Tech St", "555-0101", "contact@techsol.com", 50000.0);
        Entreprise e2 = new Entreprise(0, "Global Trade", "456 Market Ave", "555-0102", "info@globaltrade.com",
                100000.0);

        EntrepriseController.getInstance().create(e1);
        EntrepriseController.getInstance().create(e2);

        System.out.println("Test data loaded.");
    }
}
