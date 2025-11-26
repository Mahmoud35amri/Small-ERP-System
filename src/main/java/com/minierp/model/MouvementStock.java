package com.minierp.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class MouvementStock implements Identifiable {
    private int id;
    private int produitId;
    private String type; // AJOUT, RETRAIT, RESERVATION, LIBERATION
    private int quantite;
    private LocalDateTime date;

    public MouvementStock() {
    }

    public MouvementStock(int id, int produitId, String type, int quantite, LocalDateTime date) {
        this.id = id;
        this.produitId = produitId;
        this.type = type;
        this.quantite = quantite;
        this.date = date;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MouvementStock that = (MouvementStock) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MouvementStock{" +
                "id=" + id +
                ", produitId=" + produitId +
                ", type='" + type + '\'' +
                ", quantite=" + quantite +
                '}';
    }
}
