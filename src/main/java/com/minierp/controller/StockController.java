package com.minierp.controller;

import com.minierp.dao.StockDAO;
import com.minierp.model.MouvementStock;
import com.minierp.model.Stock;
import java.util.List;

public class StockController {
    private static StockController instance;
    private final StockDAO stockDAO;

    private StockController() {
        this.stockDAO = new StockDAO();
    }

    public static synchronized StockController getInstance() {
        if (instance == null) {
            instance = new StockController();
        }
        return instance;
    }

    public void ajouter(int produitId, int qte) {
        stockDAO.ajouter(produitId, qte);
    }

    public void retirer(int produitId, int qte) {
        stockDAO.retirer(produitId, qte);
    }

    public void reserver(int produitId, int qte) {
        stockDAO.reserver(produitId, qte);
    }

    public void libererReservation(int produitId, int qte) {
        stockDAO.libererReservation(produitId, qte);
    }

    public void initialiserStock(int produitId, int qte) {
        stockDAO.initialiserStock(produitId, qte);
    }

    public List<MouvementStock> historiser(int produitId) {
        return stockDAO.historiser(produitId);
    }

    public Stock getStockByProduit(int produitId) {
        return stockDAO.getStockByProduit(produitId);
    }
}
