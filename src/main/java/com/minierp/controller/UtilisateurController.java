package com.minierp.controller;

import com.minierp.model.Utilisateur;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurController {
    private static UtilisateurController instance;
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    private UtilisateurController() {
    }

    public static synchronized UtilisateurController getInstance() {
        if (instance == null) {
            instance = new UtilisateurController();
        }
        return instance;
    }

    public Utilisateur authentifier(String email, String password) throws Exception {
        return utilisateurs.stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password) && u.isActif() == true)
                .findFirst()
                .orElseThrow(() -> new Exception("Invalid credentials"));
    }

    public void create(Utilisateur u) {
        if (u.getId() == 0) {
            u.setId(utilisateurs.size() + 1);
        }
        utilisateurs.add(u);
    }

    public List<Utilisateur> getAll() {
        return new ArrayList<>(utilisateurs);
    }

    public void update(Utilisateur u) {
        for (int i = 0; i < utilisateurs.size(); i++) {
            if (utilisateurs.get(i).getId() == u.getId()) {
                utilisateurs.set(i, u);
                return;
            }
        }
    }

    public void delete(Utilisateur u) {
        utilisateurs.removeIf(user -> user.getId() == u.getId());
    }

    public void resetPassword(Utilisateur u) {
        u.setPassword("123456"); // Default reset password
        update(u);
    }

    public void toggleLock(Utilisateur u) {
        u.setActif(!u.isActif());
        update(u);
    }
}
