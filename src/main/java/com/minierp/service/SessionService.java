package com.minierp.service;

import com.minierp.controller.UtilisateurController;
import com.minierp.model.Utilisateur;
import java.util.Arrays;

public class SessionService {
    private static SessionService instance;
    private Utilisateur connectedUser;

    private SessionService() {
    }

    public static synchronized SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
        }
        return instance;
    }

    public Utilisateur login(String email, String password) throws Exception {
        Utilisateur user = UtilisateurController.getInstance().authentifier(email, password);
        this.connectedUser = user;
        return user;
    }

    public void logout() {
        this.connectedUser = null;
    }

    public Utilisateur getConnectedUser() {
        return connectedUser;
    }

    public boolean hasRole(String... roles) {
        if (connectedUser == null)
            return false;
        return Arrays.asList(roles).contains(connectedUser.getRole());
    }
}
