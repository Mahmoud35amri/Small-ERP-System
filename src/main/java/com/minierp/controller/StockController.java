package com.minierp.controller;

import com.minierp.model.MouvementStock;
import com.minierp.model.Stock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class StockController {
    private static StockController instance;
    private final List<Stock> stocks = new CopyOnWriteArrayList<>();
    private final List<MouvementStock> mouvements = new CopyOnWriteArrayList<>();
    private final AtomicInteger stockIdGenerator = new AtomicInteger(1);
    private final AtomicInteger mouvementIdGenerator = new AtomicInteger(1);

    private StockController() {
    }

    public static synchronized StockController getInstance() {
        if (instance == null) {
            instance = new StockController();
        }
        return instance;
    }

    public void ajouter(int produitId, int qte) {
        Stock stock = getStockByProduit(produitId);
        stock.setQuantiteActuelle(stock.getQuantiteActuelle() + qte);

        MouvementStock mvt = new MouvementStock(mouvementIdGenerator.getAndIncrement(), produitId, "AJOUT", qte,
                LocalDateTime.now());
        mouvements.add(mvt);

        // Sync with Produit
        ProduitController.getInstance().ajusterStock(produitId, qte);
    }

    public void retirer(int produitId, int qte) {
        Stock stock = getStockByProduit(produitId);
        if (stock.getQuantiteActuelle() < qte) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        stock.setQuantiteActuelle(stock.getQuantiteActuelle() - qte);

        MouvementStock mvt = new MouvementStock(mouvementIdGenerator.getAndIncrement(), produitId, "RETRAIT", qte,
                LocalDateTime.now());
        mouvements.add(mvt);

        // Sync with Produit
        ProduitController.getInstance().ajusterStock(produitId, -qte);
    }

    public void reserver(int produitId, int qte) {
        Stock stock = getStockByProduit(produitId);
        if (stock.getQuantiteActuelle() - stock.getQuantiteReservee() < qte) {
            throw new IllegalArgumentException("Insufficient available stock for reservation");
        }
        stock.setQuantiteReservee(stock.getQuantiteReservee() + qte);

        MouvementStock mvt = new MouvementStock(mouvementIdGenerator.getAndIncrement(), produitId, "RESERVATION", qte,
                LocalDateTime.now());
        mouvements.add(mvt);
    }

    public void libererReservation(int produitId, int qte) {
        Stock stock = getStockByProduit(produitId);
        if (stock.getQuantiteReservee() < qte) {
            throw new IllegalArgumentException("Cannot release more than reserved");
        }
        stock.setQuantiteReservee(stock.getQuantiteReservee() - qte);

        MouvementStock mvt = new MouvementStock(mouvementIdGenerator.getAndIncrement(), produitId, "LIBERATION", qte,
                LocalDateTime.now());
        mouvements.add(mvt);
    }

    public void initialiserStock(int produitId, int qte) {
        Stock stock = getStockByProduit(produitId);
        stock.setQuantiteActuelle(qte);
        MouvementStock mvt = new MouvementStock(mouvementIdGenerator.getAndIncrement(), produitId, "INITIAL", qte,
                LocalDateTime.now());
        mouvements.add(mvt);
    }

    public List<MouvementStock> historiser(int produitId) {
        return mouvements.stream()
                .filter(m -> m.getProduitId() == produitId)
                .collect(Collectors.toList());
    }

    public Stock getStockByProduit(int produitId) {
        return stocks.stream()
                .filter(s -> s.getProduitId() == produitId)
                .findFirst()
                .orElseGet(() -> {
                    Stock s = new Stock(stockIdGenerator.getAndIncrement(), produitId, 0, 0);
                    stocks.add(s);
                    return s;
                });
    }
}
