package com.minierp.controller;

import com.minierp.dao.ProduitDAO;
import com.minierp.model.Produit;
import java.util.List;

public class ProduitController {
    private static ProduitController instance;
    private final ProduitDAO produitDAO;

    private ProduitController() {
        this.produitDAO = new ProduitDAO();
    }

    public static synchronized ProduitController getInstance() {
        if (instance == null) {
            instance = new ProduitController();
        }
        return instance;
    }

    public Produit creer(Produit p) {
        return produitDAO.creer(p);
    }

    public Produit modifier(Produit p) {
        return produitDAO.modifier(p);
    }

    public List<Produit> lister() {
        return produitDAO.lister();
    }

    public void ajusterStock(int id, int delta) {
        produitDAO.ajusterStock(id, delta);
    }

    public void appliquerPromotion(int id, double pourcentage) {
        produitDAO.appliquerPromotion(id, pourcentage);
    }

    public void annulerPromotion(int id) {
        produitDAO.annulerPromotion(id);
    }

    public void supprimer(int id) {
        produitDAO.supprimer(id);
    }

    public List<Produit> listerEnRupture() {
        return produitDAO.listerEnRupture();
    }
}
