package com.minierp.model;

public class Entreprise {
    private int id;
    private String nom;
    private String adresse;
    private String telephone;
    private String email;
    private double capital;

    public Entreprise() {}

    public Entreprise(int id, String nom, String adresse, String telephone, String email, double capital) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.capital = capital;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getCapital() { return capital; }
    public void setCapital(double capital) { this.capital = capital; }
}
