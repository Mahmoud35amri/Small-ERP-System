package com.minierp.ai.service;

import com.minierp.ai.executor.ActionExecutor;
import com.minierp.ai.executor.ActionResult;
import com.minierp.ai.llm.GeminiProvider;
import com.minierp.ai.model.AIRequest;
import com.minierp.ai.model.AIResponse;
import com.minierp.ai.prompt.SystemPromptBuilder;

/**
 * Main AI Service that coordinates the AI agent workflow:
 * 1. Build system prompt
 * 2. Send to LLM provider (Gemini)
 * 3. Parse JSON response
 * 4. Execute action via ActionExecutor
 * 5. Return result
 */
public class AIService {

    private static AIService instance;

    private GeminiProvider geminiProvider;
    private final ResponseParser responseParser;
    private final ActionExecutor actionExecutor;
    private String apiKey;
    private boolean initialized = false;

    private AIService() {
        this.responseParser = new ResponseParser();
        this.actionExecutor = new ActionExecutor();
    }

    public static synchronized AIService getInstance() {
        if (instance == null) {
            instance = new AIService();
        }
        return instance;
    }

    /**
     * Initialize the AI service with a Gemini API key.
     */
    public void initialize(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Gemini API key is required");
        }
        this.apiKey = apiKey;
        this.geminiProvider = new GeminiProvider(apiKey);
        this.initialized = true;
    }

    /**
     * Check if the service is initialized.
     */
    public boolean isInitialized() {
        return initialized && geminiProvider != null;
    }

    /**
     * Process a natural language request and return the result.
     * 
     * @param userMessage The user's natural language input
     * @return ActionResult containing the outcome
     */
    public ActionResult process(String userMessage) {
        if (!isInitialized()) {
            return ActionResult.error(
                    "AI Service not initialized. Please configure your Gemini API key.",
                    "NOT_INITIALIZED");
        }

        if (userMessage == null || userMessage.isBlank()) {
            return ActionResult.error("Please enter a message.", "EMPTY_MESSAGE");
        }

        try {
            // Create request
            AIRequest request = new AIRequest(userMessage);

            // Build system prompt
            String systemPrompt = SystemPromptBuilder.build();

            // Call Gemini API
            String jsonResponse = geminiProvider.complete(systemPrompt, request.getUserMessage());

            // Parse response
            AIResponse aiResponse = responseParser.parse(jsonResponse);

            // Execute action
            return actionExecutor.execute(aiResponse);

        } catch (Exception e) {
            return ActionResult.error(
                    "Error processing request: " + e.getMessage(),
                    "PROCESSING_ERROR");
        }
    }

    /**
     * Process a request and return both the parsed AI response and execution
     * result.
     * Useful for debugging/display purposes.
     */
    public ProcessResult processWithDetails(String userMessage) {
        if (!isInitialized()) {
            return new ProcessResult(
                    null,
                    ActionResult.error("AI Service not initialized.", "NOT_INITIALIZED"),
                    null);
        }

        if (userMessage == null || userMessage.isBlank()) {
            return new ProcessResult(
                    null,
                    ActionResult.error("Please enter a message.", "EMPTY_MESSAGE"),
                    null);
        }

        try {
            AIRequest request = new AIRequest(userMessage);
            String systemPrompt = SystemPromptBuilder.build();
            String jsonResponse = geminiProvider.complete(systemPrompt, request.getUserMessage());
            AIResponse aiResponse = responseParser.parse(jsonResponse);
            ActionResult result = actionExecutor.execute(aiResponse);

            return new ProcessResult(aiResponse, result, jsonResponse);

        } catch (Exception e) {
            return new ProcessResult(
                    null,
                    ActionResult.error("Error: " + e.getMessage(), "PROCESSING_ERROR"),
                    null);
        }
    }

    /**
     * Container for detailed processing results.
     */
    public static class ProcessResult {
        private final AIResponse aiResponse;
        private final ActionResult actionResult;
        private final String rawResponse;

        public ProcessResult(AIResponse aiResponse, ActionResult actionResult, String rawResponse) {
            this.aiResponse = aiResponse;
            this.actionResult = actionResult;
            this.rawResponse = rawResponse;
        }

        public AIResponse getAiResponse() {
            return aiResponse;
        }

        public ActionResult getActionResult() {
            return actionResult;
        }

        public String getRawResponse() {
            return rawResponse;
        }
    }

    /**
     * Get the current API key (masked for security).
     */
    public String getMaskedApiKey() {
        if (apiKey == null || apiKey.length() < 8) {
            return "Not configured";
        }
        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }
}
