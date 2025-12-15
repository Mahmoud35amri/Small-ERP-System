package com.minierp.ui.ai;

import com.minierp.ai.executor.ActionResult;
import com.minierp.ai.service.AIService;
import com.minierp.model.*;
import com.minierp.util.DialogHelper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.concurrent.Task;

import java.util.List;

public class AIAssistantView extends VBox {

    private final AIService aiService;
    private TextField apiKeyField;
    private TextArea chatArea;
    private TextField inputField;
    private Button sendButton;
    private Button configureButton;
    private Label statusLabel;
    private VBox configPanel;
    private boolean isConfigured = false;

    public AIAssistantView() {
        this.aiService = AIService.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setSpacing(10);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #f5f5f5;");

        Label header = new Label("🤖 Assistant IA");
        header.setFont(Font.font("System", FontWeight.BOLD, 20));
        header.setTextFill(Color.web("#2196F3"));

        Label subtitle = new Label("Posez-moi des questions sur les ventes, stocks, ou créez des commandes...");
        subtitle.setStyle("-fx-text-fill: #666;");

        configPanel = createConfigPanel();

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);
        chatArea.setStyle("-fx-font-family: 'Consolas', 'Monaco', monospace; -fx-font-size: 13px;");
        chatArea.setText("Welcome to the ERP AI Assistant!\n\n" +
                "Examples of what you can ask:\n" +
                "• \"Show me today's sales\"\n" +
                "• \"What products are running low on stock?\"\n" +
                "• \"Show pending orders\"\n" +
                "• \"Create an order for client Groupe loukil, 5 units of Iphone 15 Pro Max\"\n" +
                "• \"Create invoice for order 1\"\n\n" +
                "⚙️ Please configure your Gemini API key above to get started.\n" +
                "─────────────────────────────────────────\n");
        VBox.setVgrow(chatArea, Priority.ALWAYS);

        HBox inputBox = createInputArea();

        statusLabel = new Label("Statut : Non configuré");
        statusLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");

        getChildren().addAll(header, subtitle, new Separator(), configPanel, chatArea, inputBox, statusLabel);

        updateUIState();
    }

    private VBox createConfigPanel() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(10));
        panel.setStyle(
                "-fx-background-color: #fff; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label configLabel = new Label("🔑 Configuration API Gemini");
        configLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        HBox keyBox = new HBox(10);
        keyBox.setAlignment(Pos.CENTER_LEFT);

        apiKeyField = new TextField();
        apiKeyField.setPromptText("Entrez votre clé API Gemini...");
        apiKeyField.setPrefWidth(350);
        HBox.setHgrow(apiKeyField, Priority.ALWAYS);

        configureButton = new Button("Configurer");
        configureButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        configureButton.setOnAction(e -> configureApiKey());

        Hyperlink getKeyLink = new Hyperlink("Obtenir une clé API");
        getKeyLink.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI("https://aistudio.google.com/apikey"));
            } catch (Exception ex) {
                appendToChat("Système", "Veuillez visiter : https://aistudio.google.com/apikey");
            }
        });

        keyBox.getChildren().addAll(apiKeyField, configureButton, getKeyLink);
        panel.getChildren().addAll(configLabel, keyBox);

        return panel;
    }

    private HBox createInputArea() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(5, 0, 0, 0));

        inputField = new TextField();
        inputField.setPromptText("Tapez votre demande ici... (ex: 'Montre les ventes du jour')");
        inputField.setPrefHeight(40);
        inputField.setStyle("-fx-font-size: 14px;");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        inputField.setOnAction(e -> sendMessage());

        sendButton = new Button("Envoyer");
        sendButton.setPrefHeight(40);
        sendButton.setPrefWidth(80);
        sendButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        sendButton.setOnAction(e -> sendMessage());

        box.getChildren().addAll(inputField, sendButton);

        return box;
    }

    private void configureApiKey() {
        String apiKey = apiKeyField.getText().trim();
        if (apiKey.isEmpty()) {
            DialogHelper.showError("Veuillez entrer une clé API valide.");
            return;
        }

        try {
            aiService.initialize(apiKey);
            isConfigured = true;
            apiKeyField.setText(aiService.getMaskedApiKey());
            apiKeyField.setDisable(true);
            configureButton.setText("✓ Configuré");
            configureButton.setDisable(true);
            configureButton.setStyle("-fx-background-color: #888; -fx-text-fill: white;");

            appendToChat("Système", "✅ API Gemini configurée avec succès !");
            statusLabel.setText("Statut : Prêt");
            statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 11px;");

            updateUIState();
        } catch (Exception e) {
            DialogHelper.showError("Échec de configuration : " + e.getMessage());
        }
    }

    private void sendMessage() {
        if (!isConfigured) {
            DialogHelper.showError("Veuillez d'abord configurer votre clé API.");
            return;
        }

        String message = inputField.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        // Display user message
        appendToChat("Vous", message);
        inputField.clear();

        // Disable input while processing
        setInputEnabled(false);
        statusLabel.setText("Statut : Traitement...");

        // Process in background thread
        Task<AIService.ProcessResult> task = new Task<>() {
            @Override
            protected AIService.ProcessResult call() {
                return aiService.processWithDetails(message);
            }
        };

        task.setOnSucceeded(e -> {
            AIService.ProcessResult result = task.getValue();
            displayResult(result);
            setInputEnabled(true);
            statusLabel.setText("Statut : Prêt");
        });

        task.setOnFailed(e -> {
            appendToChat("Erreur", "Échec du traitement : " + task.getException().getMessage());
            setInputEnabled(true);
            statusLabel.setText("Statut : Erreur");
        });

        new Thread(task).start();
    }

    private void displayResult(AIService.ProcessResult result) {
        Platform.runLater(() -> {
            ActionResult actionResult = result.getActionResult();

            // Show AI interpretation if available
            if (result.getAiResponse() != null) {
                String interpretation = String.format("Action : %s (confiance : %.0f%%)",
                        result.getAiResponse().getAction(),
                        result.getAiResponse().getConfidence() * 100);
                appendToChat("IA", interpretation);
            }

            // Show result
            if (actionResult.isSuccess()) {
                appendToChat("Résultat", "✅ " + actionResult.getMessage());

                // Display data if present
                if (actionResult.getData() != null) {
                    displayData(actionResult.getData());
                }
            } else {
                appendToChat("Résultat", "❌ " + actionResult.getMessage());
            }

            appendToChat("", "─────────────────────────────────────────");
        });
    }

    private void displayData(Object data) {
        if (data instanceof List<?> list) {
            if (list.isEmpty()) {
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(list.size(), 10); i++) {
                Object item = list.get(i);
                sb.append("  • ").append(formatItem(item)).append("\n");
            }

            if (list.size() > 10) {
                sb.append("  ... et ").append(list.size() - 10).append(" autres éléments\n");
            }

            appendToChat("Data", sb.toString().trim());
        } else {
            appendToChat("Data", formatItem(data));
        }
    }

    private String formatItem(Object item) {
        if (item instanceof Commande c) {
            return String.format("Commande #%d - Client : %d - Statut : %s - Date : %s",
                    c.getId(), c.getClientId(), c.getStatus(), c.getDate());
        } else if (item instanceof Produit p) {
            return String.format("%s (Stock : %d, Prix : %.2f)",
                    p.getNom(), p.getQuantiteStock(), p.getPrixVente());
        } else if (item instanceof Facture f) {
            return String.format("Facture #%d - Commande : %d - Montant : %.2f - Statut : %s",
                    f.getId(), f.getCommandeId(), f.getMontant(), f.getStatus());
        } else if (item instanceof Client c) {
            return String.format("%s %s (%s)", c.getNom(), c.getPrenom(), c.getEmail());
        }
        return item.toString();
    }

    private void appendToChat(String sender, String message) {
        Platform.runLater(() -> {
            if (sender != null && !sender.isEmpty()) {
                chatArea.appendText("[" + sender + "] " + message + "\n");
            } else {
                chatArea.appendText(message + "\n");
            }
            chatArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    private void setInputEnabled(boolean enabled) {
        inputField.setDisable(!enabled);
        sendButton.setDisable(!enabled);
    }

    private void updateUIState() {
        boolean configured = aiService.isInitialized();
        inputField.setDisable(!configured);
        sendButton.setDisable(!configured);
    }

    public Region getView() {
        return this;
    }
}
