package com.minierp.ai.llm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Google Gemini API provider implementation.
 * Uses the Gemini REST API to generate structured JSON responses.
 */
public class GeminiProvider {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    public GeminiProvider(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Gemini API key is required");
        }
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.gson = new Gson();
    }

    /**
     * Send a completion request to Gemini API.
     * 
     * @param systemPrompt The system instructions for the AI
     * @param userMessage  The user's natural language input
     * @return The AI's JSON response as a string
     */
    public String complete(String systemPrompt, String userMessage) throws IOException, InterruptedException {
        // Build request body
        JsonObject requestBody = buildRequestBody(systemPrompt, userMessage);

        // Build HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_API_URL + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                .build();

        // Send request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Check for errors
        if (response.statusCode() != 200) {
            throw new IOException("Gemini API error: " + response.statusCode() + " - " + response.body());
        }

        // Extract text from response
        return extractTextFromResponse(response.body());
    }

    /**
     * Build the Gemini API request body.
     */
    private JsonObject buildRequestBody(String systemPrompt, String userMessage) {
        JsonObject requestBody = new JsonObject();

        // System instruction
        JsonObject systemInstruction = new JsonObject();
        JsonObject systemParts = new JsonObject();
        systemParts.addProperty("text", systemPrompt);
        JsonObject systemRole = new JsonObject();
        systemRole.add("parts", createPartsArray(systemPrompt));
        systemInstruction.add("parts", createPartsArray(systemPrompt));
        requestBody.add("systemInstruction", systemInstruction);

        // User content
        JsonObject userContent = new JsonObject();
        userContent.addProperty("role", "user");
        userContent.add("parts", createPartsArray(userMessage));

        com.google.gson.JsonArray contents = new com.google.gson.JsonArray();
        contents.add(userContent);
        requestBody.add("contents", contents);

        // Generation config - request JSON output
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.1); // Low temperature for deterministic output
        generationConfig.addProperty("topP", 0.8);
        generationConfig.addProperty("topK", 40);
        generationConfig.addProperty("maxOutputTokens", 1024);
        generationConfig.addProperty("responseMimeType", "application/json");
        requestBody.add("generationConfig", generationConfig);

        return requestBody;
    }

    /**
     * Create a parts array with a single text element.
     */
    private com.google.gson.JsonArray createPartsArray(String text) {
        JsonObject part = new JsonObject();
        part.addProperty("text", text);
        com.google.gson.JsonArray parts = new com.google.gson.JsonArray();
        parts.add(part);
        return parts;
    }

    /**
     * Extract the text content from Gemini API response.
     */
    private String extractTextFromResponse(String responseBody) throws IOException {
        try {
            JsonObject response = gson.fromJson(responseBody, JsonObject.class);

            // Check for error
            if (response.has("error")) {
                throw new IOException("Gemini API error: " + response.get("error").toString());
            }

            // Navigate: candidates[0].content.parts[0].text
            if (response.has("candidates") && response.getAsJsonArray("candidates").size() > 0) {
                JsonObject candidate = response.getAsJsonArray("candidates").get(0).getAsJsonObject();
                if (candidate.has("content")) {
                    JsonObject content = candidate.getAsJsonObject("content");
                    if (content.has("parts") && content.getAsJsonArray("parts").size() > 0) {
                        JsonObject part = content.getAsJsonArray("parts").get(0).getAsJsonObject();
                        if (part.has("text")) {
                            return part.get("text").getAsString();
                        }
                    }
                }
            }

            throw new IOException("Unable to extract text from Gemini response: " + responseBody);
        } catch (Exception e) {
            throw new IOException("Failed to parse Gemini response: " + e.getMessage(), e);
        }
    }

    /**
     * Get provider name.
     */
    public String getName() {
        return "Gemini";
    }
}
