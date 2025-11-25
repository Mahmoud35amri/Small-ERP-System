package com.minierp.controller;

import com.minierp.model.Client;
import com.minierp.service.EntrepriseRegistry;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientController {
    private static ClientController instance;

    private ClientController() {
    }

    public static synchronized ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    // Helper to get current entreprise's clients
    private List<Client> getClients() {
        return EntrepriseRegistry.current().clients;
    }

    public Client creer(Client c) {
        if (c.getNom() == null || c.getNom().isEmpty()) {
            throw new IllegalArgumentException("Nom is required");
        }
        List<Client> clients = getClients();
        // Simple ID generation
        int maxId = clients.stream().mapToInt(Client::getId).max().orElse(0);
        c.setId(maxId + 1);
        c.setDateCreation(LocalDate.now());
        clients.add(c);
        return c;
    }

    public Client modifier(Client c) {
        if (c.getId() == 0) {
            throw new IllegalArgumentException("Client ID is required for update");
        }
        List<Client> clients = getClients();
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId() == c.getId()) {
                clients.set(i, c);
                return c;
            }
        }
        throw new IllegalArgumentException("Client not found");
    }

    public Client rechercherParCode(String code) {
        return getClients().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

    public List<Client> rechercher(String filtre) {
        if (filtre == null || filtre.isEmpty())
            return lister();
        String lowerFilter = filtre.toLowerCase();
        return getClients().stream()
                .filter(c -> c.getNom().toLowerCase().contains(lowerFilter) ||
                        c.getCode().toLowerCase().contains(lowerFilter))
                .collect(Collectors.toList());
    }

    public List<Client> lister() {
        return new ArrayList<>(getClients());
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
