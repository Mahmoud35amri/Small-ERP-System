package com.minierp.controller;

import com.minierp.dao.FournisseurDAO;
import com.minierp.model.Fournisseur;
import java.util.List;

public class FournisseurController {
    private static FournisseurController instance;
    private final FournisseurDAO fournisseurDAO;

    private FournisseurController() {
        this.fournisseurDAO = new FournisseurDAO();
    }

    public static synchronized FournisseurController getInstance() {
        if (instance == null) {
            instance = new FournisseurController();
        }
        return instance;
    }

    public Fournisseur creer(Fournisseur f) {
        return fournisseurDAO.creer(f);
    }

    public Fournisseur modifier(Fournisseur f) {
        return fournisseurDAO.modifier(f);
    }

    public void supprimer(int id) {
        fournisseurDAO.supprimer(id);
    }

    public List<Fournisseur> lister() {
        return fournisseurDAO.lister();
    }

    public void evaluer(int id, int note) {
        fournisseurDAO.evaluer(id, note);
    }
}
