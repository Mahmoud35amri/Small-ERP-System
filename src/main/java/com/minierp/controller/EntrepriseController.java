package com.minierp.controller;

import com.minierp.dao.EntrepriseDAO;
import com.minierp.model.Entreprise;
import java.util.List;

public class EntrepriseController {
    private static EntrepriseController instance;
    private final EntrepriseDAO entrepriseDAO;

    private EntrepriseController() {
        this.entrepriseDAO = new EntrepriseDAO();
    }

    public static synchronized EntrepriseController getInstance() {
        if (instance == null) {
            instance = new EntrepriseController();
        }
        return instance;
    }

    public List<Entreprise> lister() {
        return entrepriseDAO.lister();
    }

    public void creer(Entreprise e) {
        entrepriseDAO.creer(e);
    }

    public void modifier(Entreprise e) {
        entrepriseDAO.modifier(e);
    }

    public void supprimer(Entreprise e) {
        entrepriseDAO.supprimer(e);
    }
}
