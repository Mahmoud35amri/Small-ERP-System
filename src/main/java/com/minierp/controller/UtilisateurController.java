package com.minierp.controller;

import com.minierp.model.Utilisateur;
import com.minierp.service.EntrepriseRegistry;
import com.minierp.model.Entreprise;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurController {
    private static UtilisateurController instance;

    private UtilisateurController() {
    }

    public static synchronized UtilisateurController getInstance() {
        if (instance == null) {
            instance = new UtilisateurController();
        }
        return instance;
    }

    // Helper to get current entreprise's users
    private List<Utilisateur> getUsers() {
        return EntrepriseRegistry.current().utilisateurs;
    }

    public Utilisateur authentifier(String email, String password) throws Exception {
        // Search across ALL entreprises for login
        for (Entreprise e : EntrepriseRegistry.getInstance().getAll().values()) {
            Optional<Utilisateur> user = e.utilisateurs.stream()
                    .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password) && u.isActif())
                    .findFirst();
            if (user.isPresent()) {
                return user.get();
            }
        }
        throw new Exception("Invalid credentials");
    }

    public void create(Utilisateur u) {
        List<Utilisateur> users = getUsers();
        if (u.getId() == 0) {
            // Simple ID generation
            int maxId = users.stream().mapToInt(Utilisateur::getId).max().orElse(0);
            u.setId(maxId + 1);
        }
        // Ensure entrepriseId is set to current if not already
        if (u.getEntrepriseId() == 0) {
            u.setEntrepriseId(EntrepriseRegistry.current().getId());
        }
        users.add(u);
    }

    public List<Utilisateur> getAll() {
        return new ArrayList<>(getUsers());
    }

    public void update(Utilisateur u) {
        List<Utilisateur> users = getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == u.getId()) {
                users.set(i, u);
                return;
            }
        }
    }

    public void delete(Utilisateur u) {
        getUsers().removeIf(user -> user.getId() == u.getId());
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
