package com.minierp.controller;

import com.minierp.model.Categorie;
import com.minierp.service.EntrepriseRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategorieController {
    private static CategorieController instance;

    private CategorieController() {
    }

    public static synchronized CategorieController getInstance() {
        if (instance == null) {
            instance = new CategorieController();
        }
        return instance;
    }

    // Helper to get current entreprise's categories
    private List<Categorie> getCategories() {
        return EntrepriseRegistry.current().categories;
    }

    public Categorie creer(Categorie c) {
        if (c.getNom() == null || c.getNom().isEmpty()) {
            throw new IllegalArgumentException("Nom is required");
        }
        List<Categorie> categories = getCategories();
        // Simple ID generation
        int maxId = categories.stream().mapToInt(Categorie::getId).max().orElse(0);
        c.setId(maxId + 1);
        categories.add(c);
        return c;
    }

    public Categorie modifier(Categorie c) {
        if (c.getId() == 0) {
            throw new IllegalArgumentException("ID is required for update");
        }
        List<Categorie> categories = getCategories();
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == c.getId()) {
                categories.set(i, c);
                return c;
            }
        }
        throw new IllegalArgumentException("Categorie not found");
    }

    public void deplacerCategorie(int id, Integer newParentId) {
        List<Categorie> categories = getCategories();
        for (Categorie c : categories) {
            if (c.getId() == id) {
                c.setParentId(newParentId);
                return;
            }
        }
        throw new IllegalArgumentException("Categorie not found");
    }

    public void supprimer(int id) {
        List<Categorie> categories = getCategories();
        for (Categorie c : categories) {
            if (c.getId() == id) {
                categories.remove(c);
                return;
            }
        }
        throw new IllegalArgumentException("Categorie not found");
    }

    public List<Categorie> lister() {
        return new ArrayList<>(getCategories());
    }

    public List<Categorie> listerEnfants(int parentId) {
        return getCategories().stream()
                .filter(c -> c.getParentId() != null && c.getParentId() == parentId)
                .collect(Collectors.toList());
    }
}
