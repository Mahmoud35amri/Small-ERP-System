package com.minierp.controller;

import com.minierp.model.Produit;
import com.minierp.service.EntrepriseRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProduitController {
    private static ProduitController instance;

    private ProduitController() {
    }

    public static synchronized ProduitController getInstance() {
        if (instance == null) {
            instance = new ProduitController();
        }
        return instance;
    }

    // Helper to get current entreprise's products
    private List<Produit> getProduits() {
        return EntrepriseRegistry.current().produits;
    }

    public Produit creer(Produit p) {
        if (p.getNom() == null || p.getNom().isEmpty()) {
            throw new IllegalArgumentException("Nom is required");
        } else if (p.getRef() == null || p.getRef().isEmpty()) {
            throw new IllegalArgumentException("Ref is required");
        } else if (p.getQuantiteStock() == 0) {
            throw new IllegalArgumentException("QuantiteStock is required");
        } else if (p.getPrixVente() == 0) {
            throw new IllegalArgumentException("PrixVente is required");
        }
        List<Produit> produits = getProduits();
        // Simple ID generation
        int maxId = produits.stream().mapToInt(Produit::getId).max().orElse(0);
        p.setId(maxId + 1);
        produits.add(p);
        StockController.getInstance().initialiserStock(p.getId(), p.getQuantiteStock());
        return p;
    }

    public Produit modifier(Produit p) {
        if (p.getId() == 0) {
            throw new IllegalArgumentException("ID is required for update");
        }
        List<Produit> produits = getProduits();
        for (int i = 0; i < produits.size(); i++) {
            if (produits.get(i).getId() == p.getId()) {
                produits.set(i, p);
                return p;
            }
        }
        throw new IllegalArgumentException("Produit not found");
    }

    public List<Produit> lister() {
        return new ArrayList<>(getProduits());
    }

    public void ajusterStock(int id, int delta) {
        List<Produit> produits = getProduits();
        for (Produit p : produits) {
            if (p.getId() == id) {
                p.setQuantiteStock(p.getQuantiteStock() + delta);
                return;
            }
        }
        throw new IllegalArgumentException("Produit not found");
    }

    public void appliquerPromotion(int id, double pourcentage) {
        List<Produit> produits = getProduits();
        for (Produit p : produits) {
            if (p.getId() == id) {
                if (!p.isEnPromotion()) {
                    p.setPrixOriginal(p.getPrixVente());
                }
                p.setEnPromotion(true);
                p.setPromotionPourcentage(pourcentage);
                p.setPrixVente(p.getPrixOriginal() * (1 - pourcentage / 100));
                return;
            }
        }
        throw new IllegalArgumentException("Produit not found");
    }

    public void annulerPromotion(int id) {
        List<Produit> produits = getProduits();
        for (Produit p : produits) {
            if (p.getId() == id) {
                if (p.isEnPromotion()) {
                    p.setPrixVente(p.getPrixOriginal());
                    p.setEnPromotion(false);
                    p.setPromotionPourcentage(0);
                    p.setPrixOriginal(0);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Produit not found");
    }

    public void supprimer(int id) {
        List<Produit> produits = getProduits();
        for (Produit p : produits) {
            if (p.getId() == id) {
                produits.remove(p);
                return;
            }
        }
        throw new IllegalArgumentException("Produit not found");
    }

    public List<Produit> listerEnRupture() {
        return getProduits().stream()
                .filter(p -> p.getQuantiteStock() <= 0)
                .collect(Collectors.toList());
    }
}
