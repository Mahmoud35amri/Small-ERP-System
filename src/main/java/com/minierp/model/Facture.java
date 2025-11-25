package com.minierp.model;

import java.time.LocalDate;
import java.util.Objects;

public class Facture {
    private int id;
    private int commandeId;
    private LocalDate date;
    private String status; // NON_PAYEE, PAYEE, PARTIELLE, AVOIR
    private double montant;

    public Facture() {
    }

    public Facture(int id, int commandeId, LocalDate date, String status, double montant) {
        this.id = id;
        this.commandeId = commandeId;
        this.date = date;
        this.status = status;
        this.montant = montant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(int commandeId) {
        this.commandeId = commandeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Facture facture = (Facture) o;
        return id == facture.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Facture{" +
                "id=" + id +
                ", commandeId=" + commandeId +
                ", status='" + status + '\'' +
                ", montant=" + montant +
                '}';
    }
}
