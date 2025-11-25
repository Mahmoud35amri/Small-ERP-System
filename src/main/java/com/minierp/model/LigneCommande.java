package com.minierp.model;

import java.util.Objects;

public class LigneCommande {
    private int id;
    private int produitId;
    private int quantite;
    private double prixUnitaire;
    private double totalHT;
    private double totalTTC;

    public LigneCommande() {
    }

    public LigneCommande(int id, int produitId, int quantite, double prixUnitaire, double totalHT, double totalTTC) {
        this.id = id;
        this.produitId = produitId;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.totalHT = totalHT;
        this.totalTTC = totalTTC;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public double getTotalHT() {
        return totalHT;
    }

    public void setTotalHT(double totalHT) {
        this.totalHT = totalHT;
    }

    public double getTotalTTC() {
        return totalTTC;
    }

    public void setTotalTTC(double totalTTC) {
        this.totalTTC = totalTTC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LigneCommande that = (LigneCommande) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LigneCommande{" +
                "id=" + id +
                ", produitId=" + produitId +
                ", quantite=" + quantite +
                '}';
    }
}
