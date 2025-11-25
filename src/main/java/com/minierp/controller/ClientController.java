package com.minierp.controller;

import com.minierp.model.Client;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ClientController {
    private static ClientController instance;
    private final List<Client> clients = new CopyOnWriteArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    private ClientController() {
    }

    public static synchronized ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    public Client creer(Client c) {
        if (c.getNom() == null || c.getNom().isEmpty()) {
            throw new IllegalArgumentException("Nom is required");
        }
        c.setId(idGenerator.getAndIncrement());
        c.setDateCreation(LocalDate.now());
        clients.add(c);
        return c;
    }

    public Client modifier(Client c) {
        if (c.getId() == 0) {
            throw new IllegalArgumentException("Client ID is required for update");
        }
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId() == c.getId()) {
                clients.set(i, c);
                return c;
            }
        }
        throw new IllegalArgumentException("Client not found");
    }

    public Client rechercherParCode(String code) {
        return clients.stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

    public List<Client> rechercher(String filtre) {
        if (filtre == null || filtre.isEmpty())
            return lister();
        String lowerFilter = filtre.toLowerCase();
        return clients.stream()
                .filter(c -> c.getNom().toLowerCase().contains(lowerFilter) ||
                        c.getCode().toLowerCase().contains(lowerFilter))
                .collect(Collectors.toList());
    }

    public List<Client> lister() {
        return new ArrayList<>(clients);
    }

    public double calculerChiffreAffaires(int clientId) {
        // Placeholder: In a real app, this would query invoices/payments
        return 0.0;
    }
}
