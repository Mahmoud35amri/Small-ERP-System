package com.minierp.controller;

import com.minierp.dao.FactureDAO;
import com.minierp.model.Facture;
import java.io.File;
import java.util.List;

public class FactureController {
    private static FactureController instance;
    private final FactureDAO factureDAO;

    private FactureController() {
        this.factureDAO = new FactureDAO();
    }

    public static synchronized FactureController getInstance() {
        if (instance == null) {
            instance = new FactureController();
        }
        return instance;
    }

    public Facture genererDepuisCommande(int commandeId) {
        return factureDAO.genererDepuisCommande(commandeId);
    }

    public void marquerPayee(int id) {
        factureDAO.marquerPayee(id);
    }

    public void marquerPartiellement(int id, double montantPaye) {
        factureDAO.marquerPartiellement(id, montantPaye);
    }

    public Facture genererAvoir(int factureId) {
        return factureDAO.genererAvoir(factureId);
    }

    public List<Facture> lister() {
        return factureDAO.lister();
    }

    public File genererPDF(int factureId) {
        return factureDAO.genererPDF(factureId);
    }
}
