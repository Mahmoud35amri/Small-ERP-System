package com.minierp.controller;

import com.minierp.model.Fournisseur;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FournisseurController {
    private static FournisseurController instance;
    private final List<Fournisseur> fournisseurs = new CopyOnWriteArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    private FournisseurController() {
    }

    public static synchronized FournisseurController getInstance() {
        if (instance == null) {
            instance = new FournisseurController();
        }
        return instance;
    }

    public Fournisseur creer(Fournisseur f) {
        if (f.getNomSociete() == null || f.getNomSociete().isEmpty()) {
            throw new IllegalArgumentException("Nom Societe is required");
        }
        f.setId(idGenerator.getAndIncrement());
        fournisseurs.add(f);
        return f;
    }

    public Fournisseur modifier(Fournisseur f) {
        if (f.getId() == 0) {
            throw new IllegalArgumentException("ID is required for update");
        }
        for (int i = 0; i < fournisseurs.size(); i++) {
            if (fournisseurs.get(i).getId() == f.getId()) {
                fournisseurs.set(i, f);
                return f;
            }
        }
        throw new IllegalArgumentException("Fournisseur not found");
    }

    public List<Fournisseur> lister() {
        return new ArrayList<>(fournisseurs);
    }

    public void evaluer(int id, int note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("Note must be between 1 and 5");
        }
        for (Fournisseur f : fournisseurs) {
            if (f.getId() == id) {
                f.setEvaluation(note);
                return;
            }
        }
        throw new IllegalArgumentException("Fournisseur not found");
    }
}
