package com.minierp.controller;

import com.minierp.model.Produit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ProduitController {
    private static ProduitController instance;
    private final List<Produit> produits = new CopyOnWriteArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    private ProduitController() {
    }

    public static synchronized ProduitController getInstance() {
        if (instance == null) {
            instance = new ProduitController();
        }
        return instance;
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
        p.setId(idGenerator.getAndIncrement());
        produits.add(p);
        StockController.getInstance().initialiserStock(p.getId(), p.getQuantiteStock());
        return p;
    }

    public Produit modifier(Produit p) {
        if (p.getId() == 0) {
            throw new IllegalArgumentException("ID is required for update");
        }
        for (int i = 0; i < produits.size(); i++) {
            if (produits.get(i).getId() == p.getId()) {
                produits.set(i, p);
                return p;
            }
        }
        throw new IllegalArgumentException("Produit not found");
    }

    public List<Produit> lister() {
        return new ArrayList<>(produits);
    }

    public void ajusterStock(int id, int delta) {
        for (Produit p : produits) {
            if (p.getId() == id) {
                p.setQuantiteStock(p.getQuantiteStock() + delta);
                return;
            }
        }
        throw new IllegalArgumentException("Produit not found");
    }

    public void appliquerPromotion(int id, double pourcentage) {
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
        for (Produit p : produits) {
            if (p.getId() == id) {
                produits.remove(p);
                return;
            }
        }
        throw new IllegalArgumentException("Produit not found");
    }

    public List<Produit> listerEnRupture() {
        return produits.stream()
                .filter(p -> p.getQuantiteStock() <= 0)
                .collect(Collectors.toList());
    }
}
