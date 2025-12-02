package com.minierp.dao;

import com.minierp.model.MouvementStock;
import com.minierp.model.Stock;
import com.minierp.service.EntrepriseRegistry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class StockDAO {

    private List<Stock> getStocks() {
        return EntrepriseRegistry.current().stocks;
    }

    private List<MouvementStock> getMouvements() {
        return EntrepriseRegistry.current().mouvementsStock;
    }

    public void ajouter(int produitId, int qte) {
        Stock stock = getStockByProduit(produitId);
        stock.setQuantiteActuelle(stock.getQuantiteActuelle() + qte);

        List<MouvementStock> mouvements = getMouvements();
        int newId = com.minierp.util.IdGenerator.generate(mouvements);
        MouvementStock mvt = new MouvementStock(newId, produitId, "AJOUT", qte,
                LocalDateTime.now());
        mouvements.add(mvt);

        new ProduitDAO().ajusterStock(produitId, qte);
    }

    public void retirer(int produitId, int qte) {
        Stock stock = getStockByProduit(produitId);
        if (stock.getQuantiteActuelle() < qte) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        stock.setQuantiteActuelle(stock.getQuantiteActuelle() - qte);

        List<MouvementStock> mouvements = getMouvements();
        int newId = com.minierp.util.IdGenerator.generate(mouvements);
        MouvementStock mvt = new MouvementStock(newId, produitId, "RETRAIT", qte,
                LocalDateTime.now());
        mouvements.add(mvt);

        new ProduitDAO().ajusterStock(produitId, -qte);
    }

    public void reserver(int produitId, int qte) {
        Stock stock = getStockByProduit(produitId);
        if (stock.getQuantiteActuelle() - stock.getQuantiteReservee() < qte) {
            throw new IllegalArgumentException("Insufficient available stock for reservation");
        }
        stock.setQuantiteReservee(stock.getQuantiteReservee() + qte);

        List<MouvementStock> mouvements = getMouvements();
        int newId = com.minierp.util.IdGenerator.generate(mouvements);
        MouvementStock mvt = new MouvementStock(newId, produitId, "RESERVATION", qte,
                LocalDateTime.now());
        mouvements.add(mvt);
    }

    public void libererReservation(int produitId, int qte) {
        Stock stock = getStockByProduit(produitId);
        if (stock.getQuantiteReservee() < qte) {
            throw new IllegalArgumentException("Cannot release more than reserved");
        }
        stock.setQuantiteReservee(stock.getQuantiteReservee() - qte);

        List<MouvementStock> mouvements = getMouvements();
        int newId = com.minierp.util.IdGenerator.generate(mouvements);
        MouvementStock mvt = new MouvementStock(newId, produitId, "LIBERATION", qte,
                LocalDateTime.now());
        mouvements.add(mvt);
    }

    public void initialiserStock(int produitId, int qte) {
        Stock stock = getStockByProduit(produitId);
        stock.setQuantiteActuelle(qte);

        List<MouvementStock> mouvements = getMouvements();
        int newId = com.minierp.util.IdGenerator.generate(mouvements);
        MouvementStock mvt = new MouvementStock(newId, produitId, "INITIAL", qte,
                LocalDateTime.now());
        mouvements.add(mvt);
    }

    public List<MouvementStock> historiser(int produitId) {
        return getMouvements().stream()
                .filter(m -> m.getProduitId() == produitId)
                .collect(Collectors.toList());
    }

    public Stock getStockByProduit(int produitId) {
        List<Stock> stocks = getStocks();
        return stocks.stream()
                .filter(s -> s.getProduitId() == produitId)
                .findFirst()
                .orElseGet(() -> {
                    int newId = com.minierp.util.IdGenerator.generate(stocks);
                    Stock s = new Stock(newId, produitId, 0, 0);
                    stocks.add(s);
                    return s;
                });
    }
}
