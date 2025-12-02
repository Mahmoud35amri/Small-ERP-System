package com.minierp.dao;

import com.minierp.model.Commande;
import com.minierp.model.LigneCommande;
import com.minierp.service.EntrepriseRegistry;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {

    private List<Commande> getCommandes() {
        return EntrepriseRegistry.current().commandes;
    }

    public Commande creer(Commande c) {
        List<Commande> commandes = getCommandes();
        c.setId(com.minierp.util.IdGenerator.generate(commandes));
        c.setDate(LocalDate.now());
        c.setStatus("BROUILLON");
        commandes.add(c);
        return c;
    }

    private Commande findById(int id) {
        return getCommandes().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    public void ajouterLigne(int commandeId, LigneCommande ligne) {
        Commande c = findById(commandeId);
        if (c == null)
            throw new IllegalArgumentException("Commande not found");
        if (!"BROUILLON".equals(c.getStatus())) {
            throw new IllegalStateException("Cannot modify non-draft order");
        }
        ligne.setTotalHT(ligne.getQuantite() * ligne.getPrixUnitaire());
        ligne.setTotalTTC(ligne.getTotalHT() * 1.20);
        c.getLignes().add(ligne);
    }

    public void valider(int id) {
        Commande c = findById(id);
        if (c == null)
            throw new IllegalArgumentException("Commande not found");
        if (!"BROUILLON".equals(c.getStatus())) {
            throw new IllegalStateException("Order already validated or cancelled");
        }

        StockDAO stockDAO = new StockDAO();
        for (LigneCommande ligne : c.getLignes()) {
            stockDAO.reserver(ligne.getProduitId(), ligne.getQuantite());
        }
        c.setStatus("VALIDEE");
    }

    public void annuler(int id) {
        Commande c = findById(id);
        if (c == null)
            throw new IllegalArgumentException("Commande not found");
        if ("VALIDEE".equals(c.getStatus())) {
            StockDAO stockDAO = new StockDAO();
            for (LigneCommande ligne : c.getLignes()) {
                stockDAO.libererReservation(ligne.getProduitId(), ligne.getQuantite());
            }
        }
        c.setStatus("ANNULEE");
    }

    public void livrer(int id) {
        Commande c = findById(id);
        if (c == null)
            throw new IllegalArgumentException("Commande not found");
        if (!"VALIDEE".equals(c.getStatus())) {
            throw new IllegalStateException("Order must be validated to be delivered");
        }

        StockDAO stockDAO = new StockDAO();
        for (LigneCommande ligne : c.getLignes()) {
            stockDAO.libererReservation(ligne.getProduitId(), ligne.getQuantite());
            stockDAO.retirer(ligne.getProduitId(), ligne.getQuantite());
        }
        c.setStatus("LIVREE");
    }

    public double calculerMontantHT(int id) {
        Commande c = findById(id);
        if (c == null)
            throw new IllegalArgumentException("Commande not found");
        return c.getLignes().stream().mapToDouble(LigneCommande::getTotalHT).sum();
    }

    public double calculerTVA(int id) {
        return calculerMontantHT(id) * 0.20;
    }

    public double calculerTTC(int id) {
        return calculerMontantHT(id) * 1.20;
    }

    public List<Commande> lister() {
        return new ArrayList<>(getCommandes());
    }
}
