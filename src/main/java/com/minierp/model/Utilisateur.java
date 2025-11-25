package com.minierp.model;

public class Utilisateur {
    private int id;
    private String nom;
    private String email;
    private String password; // Hashed in real app
    private String role; // ADMIN, GERANT, EMPLOYE

    private String prenom;
    private boolean actif;
    private java.time.LocalDateTime dernierConnexion;
    private int entrepriseId;

    public Utilisateur() {
    }

    public Utilisateur(int id, String nom, String prenom, String email, String password, String role, boolean actif) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.actif = actif;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public java.time.LocalDateTime getDernierConnexion() {
        return dernierConnexion;
    }

    public void setDernierConnexion(java.time.LocalDateTime dernierConnexion) {
        this.dernierConnexion = dernierConnexion;
    }

    public int getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(int entrepriseId) {
        this.entrepriseId = entrepriseId;
    }
}
