package com.minierp.dao;

import com.minierp.model.Entreprise;
import com.minierp.model.Utilisateur;
import com.minierp.service.EntrepriseRegistry;
import java.util.ArrayList;
import java.util.List;

public class EntrepriseDAO {

    public List<Entreprise> lister() {
        return new ArrayList<>(EntrepriseRegistry.getInstance().getAll().values());
    }

    public void creer(Entreprise e) {
        if (e.getId() == 0) {
            e.setId(com.minierp.util.IdGenerator.generate(EntrepriseRegistry.getInstance().getAll().values()));
        }

        EntrepriseRegistry.getInstance().register(e);

        Utilisateur admin = new Utilisateur();
        admin.setNom("Admin");
        admin.setPrenom("System");
        admin.setEmail("admin@" + e.getNom().toLowerCase().replaceAll("\\s+", "") + ".com");
        admin.setPassword("admin123");
        admin.setRole("ADMIN");
        admin.setActif(true);
        admin.setEntrepriseId(e.getId());

        e.utilisateurs.add(admin);
    }

    public void modifier(Entreprise e) {
        EntrepriseRegistry.getInstance().register(e);
    }

    public void supprimer(Entreprise e) {
        // Not implemented as per controller
    }
}
