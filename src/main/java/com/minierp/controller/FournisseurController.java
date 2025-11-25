package com.minierp.controller;

import com.minierp.model.Fournisseur;
import com.minierp.service.EntrepriseRegistry;
import java.util.ArrayList;
import java.util.List;

public class FournisseurController {
    private static FournisseurController instance;

    private FournisseurController() {
    }

    public static synchronized FournisseurController getInstance() {
        if (instance == null) {
            instance = new FournisseurController();
        }
        return instance;
    }

    // Helper to get current entreprise's suppliers
    private List<Fournisseur> getFournisseurs() {
        return EntrepriseRegistry.current().fournisseurs;
    }

    public Fournisseur creer(Fournisseur f) {
        if (f.getNomSociete() == null || f.getNomSociete().isEmpty()) {
            throw new IllegalArgumentException("Nom Societe is required");
        }
        List<Fournisseur> fournisseurs = getFournisseurs();
        // Simple ID generation
        int maxId = fournisseurs.stream().mapToInt(Fournisseur::getId).max().orElse(0);
        f.setId(maxId + 1);
        fournisseurs.add(f);
        return f;
    }

    public Fournisseur modifier(Fournisseur f) {
        if (f.getId() == 0) {
            throw new IllegalArgumentException("ID is required for update");
        }
        List<Fournisseur> fournisseurs = getFournisseurs();
        for (int i = 0; i < fournisseurs.size(); i++) {
            if (fournisseurs.get(i).getId() == f.getId()) {
                fournisseurs.set(i, f);
                return f;
            }
        }
        throw new IllegalArgumentException("Fournisseur not found");
    }

    public List<Fournisseur> lister() {
        return new ArrayList<>(getFournisseurs());
    }

    public void evaluer(int id, int note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("Note must be between 1 and 5");
        }
        List<Fournisseur> fournisseurs = getFournisseurs();
        for (Fournisseur f : fournisseurs) {
            if (f.getId() == id) {
                f.setEvaluation(note);
                return;
            }
        }
        throw new IllegalArgumentException("Fournisseur not found");
    }
}
