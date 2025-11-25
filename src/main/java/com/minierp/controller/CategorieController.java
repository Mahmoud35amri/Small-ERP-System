package com.minierp.controller;

import com.minierp.model.Categorie;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CategorieController {
    private static CategorieController instance;
    private final List<Categorie> categories = new CopyOnWriteArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    private CategorieController() {
    }

    public static synchronized CategorieController getInstance() {
        if (instance == null) {
            instance = new CategorieController();
        }
        return instance;
    }

    public Categorie creer(Categorie c) {
        if (c.getNom() == null || c.getNom().isEmpty()) {
            throw new IllegalArgumentException("Nom is required");
        }
        c.setId(idGenerator.getAndIncrement());
        categories.add(c);
        return c;
    }

    public Categorie modifier(Categorie c) {
        if (c.getId() == 0) {
            throw new IllegalArgumentException("ID is required for update");
        }
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == c.getId()) {
                categories.set(i, c);
                return c;
            }
        }
        throw new IllegalArgumentException("Categorie not found");
    }

    public void deplacerCategorie(int id, Integer newParentId) {
        for (Categorie c : categories) {
            if (c.getId() == id) {
                c.setParentId(newParentId);
                return;
            }
        }
        throw new IllegalArgumentException("Categorie not found");
    }

    public void supprimer(int id) {
        for (Categorie c : categories) {
            if (c.getId() == id) {
                categories.remove(c);
                return;
            }
        }
        throw new IllegalArgumentException("Categorie not found");
    }

    public List<Categorie> lister() {
        return new ArrayList<>(categories);
    }

    public List<Categorie> listerEnfants(int parentId) {
        return categories.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() == parentId)
                .collect(Collectors.toList());
    }
}
