package com.minierp.ai.executor;

import com.minierp.ai.AIAction;
import com.minierp.ai.model.AIResponse;

import com.minierp.dao.*;
import com.minierp.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Executes AI actions by routing them to existing ERP DAOs and Controllers.
 * The AI never accesses data directly - this executor handles all actual
 * operations.
 */
public class ActionExecutor {

    private static final int LOW_STOCK_THRESHOLD = 10; // Products with less than 10 units

    private final CommandeDAO commandeDAO;
    private final ClientDAO clientDAO;
    private final ProduitDAO produitDAO;

    private final FactureDAO factureDAO;

    public ActionExecutor() {
        this.commandeDAO = new CommandeDAO();
        this.clientDAO = new ClientDAO();
        this.produitDAO = new ProduitDAO();

        this.factureDAO = new FactureDAO();
    }

    /**
     * Execute an AI response action.
     */
    public ActionResult execute(AIResponse response) {
        if (response == null) {
            return ActionResult.error("No response to execute", "NULL_RESPONSE");
        }

        AIAction action = response.getAction();
        if (action == null || action == AIAction.UNKNOWN_ACTION) {
            return ActionResult.error(
                    "I couldn't understand your request. Please try rephrasing.",
                    "UNKNOWN_ACTION");
        }

        // Check confidence threshold
        if (response.getConfidence() < 0.5) {
            return ActionResult.error(
                    "I'm not confident enough about this request. Please be more specific.",
                    "LOW_CONFIDENCE");
        }

        try {
            return switch (action) {
                case GET_TODAY_SALES -> getTodaySales();
                case GET_MONTHLY_SALES -> getMonthlySales();
                case GET_LOW_STOCK -> getLowStock();
                case GET_PENDING_ORDERS -> getPendingOrders();
                case CREATE_ORDER -> createOrder(response);
                case CREATE_INVOICE -> createInvoice(response);
                default -> ActionResult.error("Action not implemented: " + action, "NOT_IMPLEMENTED");
            };
        } catch (Exception e) {
            return ActionResult.error("Error executing action: " + e.getMessage(), "EXECUTION_ERROR");
        }
    }

    /**
     * GET_TODAY_SALES: Get orders from today.
     */
    private ActionResult getTodaySales() {
        LocalDate today = LocalDate.now();
        List<Commande> allOrders = commandeDAO.lister();

        List<Commande> todayOrders = allOrders.stream()
                .filter(c -> c.getDate() != null && c.getDate().equals(today))
                .collect(Collectors.toList());

        if (todayOrders.isEmpty()) {
            return ActionResult.success("No sales found for today (" + today + ").", todayOrders);
        }

        return ActionResult.success(
                "Found " + todayOrders.size() + " order(s) for today (" + today + ").",
                todayOrders);
    }

    /**
     * GET_MONTHLY_SALES: Get orders from current month.
     */
    private ActionResult getMonthlySales() {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        List<Commande> allOrders = commandeDAO.lister();

        List<Commande> monthlyOrders = allOrders.stream()
                .filter(c -> c.getDate() != null &&
                        c.getDate().getMonthValue() == currentMonth &&
                        c.getDate().getYear() == currentYear)
                .collect(Collectors.toList());

        String monthName = now.getMonth().toString();
        if (monthlyOrders.isEmpty()) {
            return ActionResult.success(
                    "No sales found for " + monthName + " " + currentYear + ".",
                    monthlyOrders);
        }

        return ActionResult.success(
                "Found " + monthlyOrders.size() + " order(s) for " + monthName + " " + currentYear + ".",
                monthlyOrders);
    }

    /**
     * GET_LOW_STOCK: Get products with stock below threshold.
     */
    private ActionResult getLowStock() {
        List<Produit> allProducts = produitDAO.lister();

        List<Produit> lowStockProducts = allProducts.stream()
                .filter(p -> p.getQuantiteStock() < LOW_STOCK_THRESHOLD)
                .collect(Collectors.toList());

        if (lowStockProducts.isEmpty()) {
            return ActionResult.success(
                    "No products with low stock (threshold: " + LOW_STOCK_THRESHOLD + " units).",
                    lowStockProducts);
        }

        return ActionResult.success(
                "Found " + lowStockProducts.size() + " product(s) with low stock (below " + LOW_STOCK_THRESHOLD
                        + " units).",
                lowStockProducts);
    }

    /**
     * GET_PENDING_ORDERS: Get orders in BROUILLON (draft) status.
     */
    private ActionResult getPendingOrders() {
        List<Commande> allOrders = commandeDAO.lister();

        List<Commande> pendingOrders = allOrders.stream()
                .filter(c -> "BROUILLON".equals(c.getStatus()))
                .collect(Collectors.toList());

        if (pendingOrders.isEmpty()) {
            return ActionResult.success("No pending orders found.", pendingOrders);
        }

        return ActionResult.success(
                "Found " + pendingOrders.size() + " pending order(s).",
                pendingOrders);
    }

    /**
     * CREATE_ORDER: Create a new order.
     * Required parameters: clientName, productName, quantity
     */
    private ActionResult createOrder(AIResponse response) {
        String clientName = response.getStringParam("clientName");
        String productName = response.getStringParam("productName");
        Integer quantity = response.getIntParam("quantity");

        // Validate parameters
        if (clientName == null || clientName.isBlank()) {
            return ActionResult.error("Client name is required", "MISSING_CLIENT");
        }
        if (productName == null || productName.isBlank()) {
            return ActionResult.error("Product name is required", "MISSING_PRODUCT");
        }
        if (quantity == null || quantity <= 0) {
            return ActionResult.error("Valid quantity is required", "INVALID_QUANTITY");
        }

        // Find client by name
        List<Client> clients = clientDAO.lister();
        Client client = clients.stream()
                .filter(c -> c.getNom() != null &&
                        c.getNom().toLowerCase().contains(clientName.toLowerCase()))
                .findFirst()
                .orElse(null);

        if (client == null) {
            return ActionResult.error(
                    "Client not found: " + clientName + ". Please check the name and try again.",
                    "CLIENT_NOT_FOUND");
        }

        // Find product by name
        List<Produit> products = produitDAO.lister();
        Produit product = products.stream()
                .filter(p -> p.getNom() != null &&
                        p.getNom().toLowerCase().contains(productName.toLowerCase()))
                .findFirst()
                .orElse(null);

        if (product == null) {
            return ActionResult.error(
                    "Product not found: " + productName + ". Please check the name and try again.",
                    "PRODUCT_NOT_FOUND");
        }

        // Check stock availability
        if (product.getQuantiteStock() < quantity) {
            return ActionResult.error(
                    "Insufficient stock for " + product.getNom() + ". Available: " +
                            product.getQuantiteStock() + ", Requested: " + quantity,
                    "INSUFFICIENT_STOCK");
        }

        // Create order
        Commande order = new Commande();
        order.setClientId(client.getId());
        Commande createdOrder = commandeDAO.creer(order);

        // Add line item
        LigneCommande ligne = new LigneCommande();
        ligne.setProduitId(product.getId());
        ligne.setQuantite(quantity);
        ligne.setPrixUnitaire(product.getPrixVente());
        commandeDAO.ajouterLigne(createdOrder.getId(), ligne);

        return ActionResult.success(
                "Order #" + createdOrder.getId() + " created for " + client.getNom() +
                        ": " + quantity + "x " + product.getNom(),
                createdOrder);
    }

    /**
     * CREATE_INVOICE: Create an invoice from an order.
     * Required parameters: orderId
     */
    private ActionResult createInvoice(AIResponse response) {
        Integer orderId = response.getIntParam("orderId");

        if (orderId == null) {
            return ActionResult.error("Order ID is required", "MISSING_ORDER_ID");
        }

        // Verify order exists
        List<Commande> orders = commandeDAO.lister();
        Commande order = orders.stream()
                .filter(c -> c.getId() == orderId)
                .findFirst()
                .orElse(null);

        if (order == null) {
            return ActionResult.error("Order not found: #" + orderId, "ORDER_NOT_FOUND");
        }

        // Generate invoice
        try {
            Facture invoice = factureDAO.genererDepuisCommande(orderId);
            return ActionResult.success(
                    "Invoice #" + invoice.getId() + " created for Order #" + orderId +
                            ". Amount: " + String.format("%.2f", invoice.getMontant()),
                    invoice);
        } catch (Exception e) {
            return ActionResult.error(
                    "Failed to create invoice: " + e.getMessage(),
                    "INVOICE_CREATION_FAILED");
        }
    }
}
