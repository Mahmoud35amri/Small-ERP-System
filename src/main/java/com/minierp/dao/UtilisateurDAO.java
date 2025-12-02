package com.minierp.dao;

import com.minierp.model.Utilisateur;
import com.minierp.service.EntrepriseRegistry;
import com.minierp.model.Entreprise;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurDAO {

    private List<Utilisateur> getUsers() {
        return EntrepriseRegistry.current().utilisateurs;
    }

    public Utilisateur authentifier(String email, String password) throws Exception {
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

    public void creer(Utilisateur u) {
        List<Utilisateur> users = getUsers();
        if (u.getId() == 0) {
            u.setId(com.minierp.util.IdGenerator.generate(users));
        }
        if (u.getEntrepriseId() == 0) {
            u.setEntrepriseId(EntrepriseRegistry.current().getId());
        }
        users.add(u);
    }

    public List<Utilisateur> lister() {
        return new ArrayList<>(getUsers());
    }

    public void modifier(Utilisateur u) {
        List<Utilisateur> users = getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == u.getId()) {
                users.set(i, u);
                return;
            }
        }
    }

    public void supprimer(Utilisateur u) {
        getUsers().removeIf(user -> user.getId() == u.getId());
    }

    public void resetPassword(Utilisateur u) {
        u.setPassword("123456");
        modifier(u);
    }

    public void toggleLock(Utilisateur u) {
        u.setActif(!u.isActif());
        modifier(u);
    }
}
