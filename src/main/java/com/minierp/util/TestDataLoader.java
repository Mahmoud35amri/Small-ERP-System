package com.minierp.util;

import com.minierp.controller.EntrepriseController;
import com.minierp.controller.UtilisateurController;
import com.minierp.model.Entreprise;
import com.minierp.model.Utilisateur;
import com.minierp.service.EntrepriseRegistry;

public class TestDataLoader {
    public static void load() {
        // 1. Create Entreprises
        // Note: This automatically creates an admin user for each entreprise
        Entreprise e1 = new Entreprise(0, "Tech Solutions", "123 Tech St", "555-0101", "contact@techsol.com", 50000.0);
        Entreprise e2 = new Entreprise(0, "Global Trade", "456 Market Ave", "555-0102", "info@globaltrade.com",
                100000.0);

        EntrepriseController.getInstance().create(e1);
        EntrepriseController.getInstance().create(e2);

        // 2. Add data for Tech Solutions
        EntrepriseRegistry.getInstance().switchTo(e1.getId());

        Utilisateur user1 = new Utilisateur(0, "John", "Doe", "john@techsol.com", "123456", "EMPLOYE", true);
        user1.setEntrepriseId(e1.getId());
        UtilisateurController.getInstance().create(user1);

        // Add more data for e1 if needed (Clients, Products, etc.)

        // 3. Add data for Global Trade
        EntrepriseRegistry.getInstance().switchTo(e2.getId());

        Utilisateur user2 = new Utilisateur(0, "Jane", "Smith", "jane@globaltrade.com", "123456", "GERANT", true);
        user2.setEntrepriseId(e2.getId());
        UtilisateurController.getInstance().create(user2);

        System.out.println("Test data loaded for multi-tenancy.");
    }
}
