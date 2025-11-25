package com.minierp.controller;

import com.minierp.model.Facture;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FactureController {
    private static FactureController instance;
    private final List<Facture> factures = new CopyOnWriteArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    private FactureController() {
    }

    public static synchronized FactureController getInstance() {
        if (instance == null) {
            instance = new FactureController();
        }
        return instance;
    }

    public Facture genererDepuisCommande(int commandeId) {
        double montant = CommandeController.getInstance().calculerTTC(commandeId);
        Facture f = new Facture(idGenerator.getAndIncrement(), commandeId, LocalDate.now(), "NON_PAYEE", montant);
        factures.add(f);
        return f;
    }

    private Facture findById(int id) {
        return factures.stream().filter(f -> f.getId() == id).findFirst().orElse(null);
    }

    public void marquerPayee(int id) {
        Facture f = findById(id);
        if (f == null)
            throw new IllegalArgumentException("Facture not found");
        f.setStatus("PAYEE");
    }

    public void marquerPartiellement(int id, double montantPaye) {
        Facture f = findById(id);
        if (f == null)
            throw new IllegalArgumentException("Facture not found");
        f.setStatus("PARTIELLE");
        // Logic to track partial payment could be added here
    }

    public Facture genererAvoir(int factureId) {
        Facture f = findById(factureId);
        if (f == null)
            throw new IllegalArgumentException("Facture not found");
        Facture avoir = new Facture(idGenerator.getAndIncrement(), f.getCommandeId(), LocalDate.now(), "AVOIR",
                -f.getMontant());
        factures.add(avoir);
        return avoir;
    }

    public List<Facture> lister() {
        return new ArrayList<>(factures);
    }

    public File genererPDF(int factureId) {
        // Placeholder
        return new File("facture_" + factureId + ".pdf");
    }
}
