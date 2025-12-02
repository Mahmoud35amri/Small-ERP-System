package com.minierp.controller;

import com.minierp.dao.CategorieDAO;
import com.minierp.model.Categorie;
import java.util.List;

public class CategorieController {
    private static CategorieController instance;
    private final CategorieDAO categorieDAO;

    private CategorieController() {
        this.categorieDAO = new CategorieDAO();
    }

    public static synchronized CategorieController getInstance() {
        if (instance == null) {
            instance = new CategorieController();
        }
        return instance;
    }

    public Categorie creer(Categorie c) {
        return categorieDAO.creer(c);
    }

    public Categorie modifier(Categorie c) {
        return categorieDAO.modifier(c);
    }

    public void deplacerCategorie(int id, Integer newParentId) {
        categorieDAO.deplacerCategorie(id, newParentId);
    }

    public void supprimer(int id) {
        categorieDAO.supprimer(id);
    }

    public List<Categorie> lister() {
        return categorieDAO.lister();
    }

    public List<Categorie> listerEnfants(int parentId) {
        return categorieDAO.listerEnfants(parentId);
    }
}
