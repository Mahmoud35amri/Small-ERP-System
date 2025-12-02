package com.minierp.controller;

import com.minierp.dao.UtilisateurDAO;
import com.minierp.model.Utilisateur;
import java.util.List;

public class UtilisateurController {
    private static UtilisateurController instance;
    private final UtilisateurDAO utilisateurDAO;

    private UtilisateurController() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public static synchronized UtilisateurController getInstance() {
        if (instance == null) {
            instance = new UtilisateurController();
        }
        return instance;
    }

    public Utilisateur authentifier(String email, String password) throws Exception {
        return utilisateurDAO.authentifier(email, password);
    }

    public void creer(Utilisateur u) {
        utilisateurDAO.creer(u);
    }

    public List<Utilisateur> lister() {
        return utilisateurDAO.lister();
    }

    public void modifier(Utilisateur u) {
        utilisateurDAO.modifier(u);
    }

    public void supprimer(Utilisateur u) {
        utilisateurDAO.supprimer(u);
    }

    public void resetPassword(Utilisateur u) {
        utilisateurDAO.resetPassword(u);
    }

    public void toggleLock(Utilisateur u) {
        utilisateurDAO.toggleLock(u);
    }
}
