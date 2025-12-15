package com.minierp.dao;

import com.minierp.model.Facture;
import com.minierp.model.*;
import com.minierp.service.EntrepriseRegistry;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
        Facture f = findById(factureId);
        if (f == null)
            return null;

        Commande cmd = EntrepriseRegistry.current().commandes.stream()
                .filter(c -> c.getId() == f.getCommandeId())
                .findFirst().orElse(null);

        Client client = null;
        if (cmd != null) {
            client = EntrepriseRegistry.current().clients.stream()
                    .filter(c -> c.getId() == cmd.getClientId())
                    .findFirst().orElse(null);
        }

        String fileName = "facture_" + factureId + ".html";
        File file = new File(fileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("<html><head><title>Facture #" + factureId + "</title>");
            writer.println("<style>");
            writer.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            writer.println("h1 { color: #2c3e50; }");
            writer.println(".header { margin-bottom: 30px; }");
            writer.println(".details { margin-bottom: 20px; }");
            writer.println("table { width: 100%; border-collapse: collapse; }");
            writer.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            writer.println("th { background-color: #f2f2f2; }");
            writer.println(".total { font-weight: bold; text-align: right; margin-top: 20px; font-size: 1.2em; }");
            writer.println("</style></head><body>");

            writer.println("<div class='header'>");
            writer.println("<h1>Facture N° " + factureId + "</h1>");
            writer.println("<p>Date: " + f.getDate() + "</p>");
            writer.println("<p>Status: " + f.getStatus() + "</p>");
            writer.println("</div>");

            if (client != null) {
                writer.println("<div class='details'>");
                writer.println("<h3>Client:</h3>");
                writer.println("<p>" + client.getNom() + " " + client.getPrenom() + "<br>");
                writer.println(client.getAdresse() + "<br>");
                writer.println("Tel: " + client.getPhone() + "</p>");
                writer.println("</div>");
            }

            writer.println("<table>");
            writer.println("<tr><th>Produit</th><th>Qté</th><th>Prix Unit.</th><th>Total</th></tr>");

            if (cmd != null) {
                for (com.minierp.model.LigneCommande ligne : cmd.getLignes()) {
                    com.minierp.model.Produit p = EntrepriseRegistry.current().produits.stream()
                            .filter(prod -> prod.getId() == ligne.getProduitId())
                            .findFirst().orElse(null);

                    String prodName = (p != null) ? p.getNom() : "Produit #" + ligne.getProduitId();

                    writer.println("<tr>");
                    writer.println("<td>" + prodName + "</td>");
                    writer.println("<td>" + ligne.getQuantite() + "</td>");
                    writer.println("<td>" + String.format("%.2f", ligne.getPrixUnitaire()) + "</td>");
                    writer.println(
                            "<td>" + String.format("%.2f", ligne.getQuantite() * ligne.getPrixUnitaire()) + "</td>");
                    writer.println("</tr>");
                }
            }
            writer.println("</table>");

            writer.println("<div class='total'>");
            writer.println("Total à payer: " + String.format("%.2f", f.getMontant()) + " TND");
            writer.println("</div>");

            writer.println("</body></html>");

            return file;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
