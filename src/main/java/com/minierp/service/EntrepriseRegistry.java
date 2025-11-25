package com.minierp.service;

import com.minierp.model.Entreprise;
import java.util.HashMap;
import java.util.Map;

public class EntrepriseRegistry {
    private static EntrepriseRegistry instance;
    private final Map<Integer, Entreprise> entreprises = new HashMap<>();
    private Entreprise currentEntreprise;

    private EntrepriseRegistry() {
    }

    public static synchronized EntrepriseRegistry getInstance() {
        if (instance == null) {
            instance = new EntrepriseRegistry();
        }
        return instance;
    }

    public void register(Entreprise e) {
        entreprises.put(e.getId(), e);
        if (currentEntreprise == null) {
            currentEntreprise = e;
        }
    }

    public Entreprise get(int id) {
        return entreprises.get(id);
    }

    public void switchTo(int id) {
        Entreprise e = entreprises.get(id);
        if (e != null) {
            currentEntreprise = e;
        } else {
            throw new IllegalArgumentException("Entreprise not found: " + id);
        }
    }

    public static Entreprise current() {
        if (getInstance().currentEntreprise == null) {
            throw new IllegalStateException("No current entreprise selected");
        }
        return getInstance().currentEntreprise;
    }

    public Map<Integer, Entreprise> getAll() {
        return new HashMap<>(entreprises);
    }
}
