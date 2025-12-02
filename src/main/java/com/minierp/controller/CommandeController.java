package com.minierp.controller;

import com.minierp.dao.CommandeDAO;
import com.minierp.model.Commande;
import com.minierp.model.LigneCommande;
import java.util.List;

public class CommandeController {
    private static CommandeController instance;
    private final CommandeDAO commandeDAO;

    private CommandeController() {
        this.commandeDAO = new CommandeDAO();
    }

    public static synchronized CommandeController getInstance() {
        if (instance == null) {
            instance = new CommandeController();
        }
        return instance;
    }

    public Commande creer(Commande c) {
        return commandeDAO.creer(c);
    }

    public void ajouterLigne(int commandeId, LigneCommande ligne) {
        commandeDAO.ajouterLigne(commandeId, ligne);
    }

    public void valider(int id) {
        commandeDAO.valider(id);
    }

    public void annuler(int id) {
        commandeDAO.annuler(id);
    }

    public void livrer(int id) {
        commandeDAO.livrer(id);
    }

    public double calculerMontantHT(int id) {
        return commandeDAO.calculerMontantHT(id);
    }

    public double calculerTVA(int id) {
        return commandeDAO.calculerTVA(id);
    }

    public double calculerTTC(int id) {
        return commandeDAO.calculerTTC(id);
    }

    public List<Commande> lister() {
        return commandeDAO.lister();
    }
}
