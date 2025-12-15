package com.minierp.ai.model;

/**
 * Wrapper for user's natural language input to the AI agent.
 */
public class AIRequest {
    private final String userMessage;
    private final String language; // "en" or "fr"

    public AIRequest(String userMessage) {
        this.userMessage = userMessage;
        this.language = detectLanguage(userMessage);
    }

    public AIRequest(String userMessage, String language) {
        this.userMessage = userMessage;
        this.language = language;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getLanguage() {
        return language;
    }

    /**
     * Simple language detection based on common French words.
     */
    private String detectLanguage(String text) {
        if (text == null)
            return "en";
        String lower = text.toLowerCase();
        // Common French words/patterns
        if (lower.contains("commande") || lower.contains("facture") ||
                lower.contains("créer") || lower.contains("afficher") ||
                lower.contains("aujourd'hui") || lower.contains("mois") ||
                lower.contains("produit") || lower.contains("client") ||
                lower.contains("ventes") || lower.contains("stock")) {
            return "fr";
        }
        return "en";
    }

    @Override
    public String toString() {
        return "AIRequest{" +
                "userMessage='" + userMessage + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
