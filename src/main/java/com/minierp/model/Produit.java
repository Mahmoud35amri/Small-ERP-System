package com.minierp.model;

import java.util.Objects;

public class Produit {
    private int id;
    private String ref;
    private String nom;
    private int categorieId;
    private int quantiteStock;
    private double prixVente;
    private boolean enPromotion;
    private double promotionPourcentage;
    private double prixOriginal;
    private boolean bestSeller;

    public Produit() {
    }

    public Produit(int id, String ref, String nom, int categorieId, int quantiteStock, double prixVente,
            double prixPromotion,
            boolean enPromotion, double promotionPourcentage, boolean bestSeller) {
        this.id = id;
        this.ref = ref;
        this.nom = nom;
        this.categorieId = categorieId;
        this.quantiteStock = quantiteStock;
        this.prixVente = prixVente;
        this.enPromotion = enPromotion;
        this.promotionPourcentage = promotionPourcentage;
        this.bestSeller = bestSeller;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public double getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }

    public boolean isEnPromotion() {
        return enPromotion;
    }

    public void setEnPromotion(boolean enPromotion) {
        this.enPromotion = enPromotion;
    }

    public double getPromotionPourcentage() {
        return promotionPourcentage;
    }

    public void setPromotionPourcentage(double promotionPourcentage) {
        this.promotionPourcentage = promotionPourcentage;
    }

    public double getPrixOriginal() {
        return prixOriginal;
    }

    public void setPrixOriginal(double prixOriginal) {
        this.prixOriginal = prixOriginal;
    }

    public boolean isBestSeller() {
        return bestSeller;
    }

    public void setBestSeller(boolean bestSeller) {
        this.bestSeller = bestSeller;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Produit produit = (Produit) o;
        return id == produit.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", ref='" + ref + '\'' +
                ", nom='" + nom + '\'' +
                '}';
    }
}
