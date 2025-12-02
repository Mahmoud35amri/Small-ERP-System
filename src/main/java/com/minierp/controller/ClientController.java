package com.minierp.controller;

import com.minierp.dao.ClientDAO;
import com.minierp.model.Client;
import java.util.List;

public class ClientController {
    private static ClientController instance;
    private final ClientDAO clientDAO;

    private ClientController() {
        this.clientDAO = new ClientDAO();
    }

    public static synchronized ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    public Client creer(Client c) {
        return clientDAO.creer(c);
    }

    public Client modifier(Client c) {
        return clientDAO.modifier(c);
    }

    public void supprimer(int id) {
        clientDAO.supprimer(id);
    }

    public Client rechercherParCode(String code) {
        return clientDAO.rechercherParCode(code);
    }

    public List<Client> rechercher(String filtre) {
        return clientDAO.rechercher(filtre);
    }

    public List<Client> lister() {
        return clientDAO.lister();
    }

    public double calculerChiffreAffaires(int clientId) {
        return FactureController.getInstance().lister().stream()
                .filter(f -> "PAYEE".equals(f.getStatus()))
                .filter(f -> {
                    com.minierp.model.Commande c = CommandeController.getInstance().lister().stream()
                            .filter(cmd -> cmd.getId() == f.getCommandeId())
                            .findFirst().orElse(null);
                    return c != null && c.getClientId() == clientId;
                })
                .mapToDouble(com.minierp.model.Facture::getMontant)
                .sum();
    }
}
