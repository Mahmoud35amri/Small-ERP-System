package com.minierp.dao;

import com.minierp.model.Facture;
import com.minierp.service.EntrepriseRegistry;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FactureDAO {

    private List<Facture> getFactures() {
        return EntrepriseRegistry.current().factures;
    }

    public Facture genererDepuisCommande(int commandeId) {
        CommandeDAO commandeDAO = new CommandeDAO();
        double montant = commandeDAO.calculerTTC(commandeId);
        List<Facture> factures = getFactures();
        int newId = com.minierp.util.IdGenerator.generate(factures);
        Facture f = new Facture(newId, commandeId, LocalDate.now(), "NON_PAYEE", montant);
        factures.add(f);
        return f;
    }

    private Facture findById(int id) {
        return getFactures().stream().filter(f -> f.getId() == id).findFirst().orElse(null);
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
    }

    public Facture genererAvoir(int factureId) {
        Facture f = findById(factureId);
        if (f == null)
            throw new IllegalArgumentException("Facture not found");
        List<Facture> factures = getFactures();
        int newId = com.minierp.util.IdGenerator.generate(factures);
        Facture avoir = new Facture(newId, f.getCommandeId(), LocalDate.now(), "AVOIR",
                -f.getMontant());
        factures.add(avoir);
        return avoir;
    }

    public List<Facture> lister() {
        return new ArrayList<>(getFactures());
    }

    public File genererPDF(int factureId) {
        return new File("facture_" + factureId + ".pdf");
    }
}
