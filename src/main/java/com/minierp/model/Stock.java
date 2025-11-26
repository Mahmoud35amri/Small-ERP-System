package com.minierp.model;

import java.util.Objects;

public class Stock implements Identifiable {
    private int id;
    private int produitId;
    private int quantiteActuelle;
    private int quantiteReservee;

    public Stock() {
    }

    public Stock(int id, int produitId, int quantiteActuelle, int quantiteReservee) {
        this.id = id;
        this.produitId = produitId;
        this.quantiteActuelle = quantiteActuelle;
        this.quantiteReservee = quantiteReservee;
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

    public int getQuantiteActuelle() {
        return quantiteActuelle;
    }

    public void setQuantiteActuelle(int quantiteActuelle) {
        this.quantiteActuelle = quantiteActuelle;
    }

    public int getQuantiteReservee() {
        return quantiteReservee;
    }

    public void setQuantiteReservee(int quantiteReservee) {
        this.quantiteReservee = quantiteReservee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Stock stock = (Stock) o;
        return id == stock.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", produitId=" + produitId +
                ", quantiteActuelle=" + quantiteActuelle +
                '}';
    }
}
