package com.minierp.model;

import java.util.Objects;

public class Fournisseur {
    private int id;
    private String code;
    private String nomSociete;
    private String contact;
    private String phone;
    private String conditions;
    private int delaiLivraison;
    private int evaluation; // 1-5

    public Fournisseur() {
    }

    public Fournisseur(int id, String code, String nomSociete, String contact, String phone, String conditions,
            int delaiLivraison, int evaluation) {
        this.id = id;
        this.code = code;
        this.nomSociete = nomSociete;
        this.contact = contact;
        this.phone = phone;
        this.conditions = conditions;
        this.delaiLivraison = delaiLivraison;
        this.evaluation = evaluation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNomSociete() {
        return nomSociete;
    }

    public void setNomSociete(String nomSociete) {
        this.nomSociete = nomSociete;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public int getDelaiLivraison() {
        return delaiLivraison;
    }

    public void setDelaiLivraison(int delaiLivraison) {
        this.delaiLivraison = delaiLivraison;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Fournisseur that = (Fournisseur) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Fournisseur{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", nomSociete='" + nomSociete + '\'' +
                '}';
    }
}
