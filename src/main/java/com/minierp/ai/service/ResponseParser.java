package com.minierp.ai.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.minierp.ai.AIAction;
import com.minierp.ai.model.AIResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses JSON responses from the AI into AIResponse objects.
 */
public class ResponseParser {

    private final Gson gson;

    public ResponseParser() {
        this.gson = new Gson();
    }

    /**
     * Parse the AI's JSON response into an AIResponse object.
     * 
     * @param jsonResponse The raw JSON string from the AI
     * @return Parsed AIResponse, or UNKNOWN_ACTION response if parsing fails
     */
    public AIResponse parse(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isBlank()) {
            return createUnknownResponse("Empty response from AI");
        }

        try {
            // Clean the response - remove any markdown code blocks if present
            String cleanJson = cleanJsonResponse(jsonResponse);

            // Parse JSON
            JsonObject json = gson.fromJson(cleanJson, JsonObject.class);

            if (json == null) {
                return createUnknownResponse("Invalid JSON structure");
            }

            AIResponse response = new AIResponse();

            // Parse action
            if (json.has("action")) {
                String actionStr = json.get("action").getAsString();
                response.setAction(AIAction.fromString(actionStr));
            } else {
                response.setAction(AIAction.UNKNOWN_ACTION);
            }

            // Parse confidence
            if (json.has("confidence")) {
                try {
                    response.setConfidence(json.get("confidence").getAsDouble());
                } catch (Exception e) {
                    response.setConfidence(0.5);
                }
            }

            // Parse parameters
            if (json.has("parameters") && json.get("parameters").isJsonObject()) {
                Map<String, Object> params = parseParameters(json.getAsJsonObject("parameters"));
                response.setParameters(params);
            }

            return response;

        } catch (JsonSyntaxException e) {
            return createUnknownResponse("JSON parsing error: " + e.getMessage());
        } catch (Exception e) {
            return createUnknownResponse("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Clean JSON response by removing markdown code blocks.
     */
    private String cleanJsonResponse(String response) {
        String cleaned = response.trim();

        // Remove markdown code blocks
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        return cleaned.trim();
    }

    /**
     * Parse parameters JSON object into a Map.
     */
    private Map<String, Object> parseParameters(JsonObject paramsJson) {
        Map<String, Object> params = new HashMap<>();

        for (String key : paramsJson.keySet()) {
            try {
                var element = paramsJson.get(key);
                if (element.isJsonNull()) {
                    params.put(key, null);
                } else if (element.isJsonPrimitive()) {
                    var primitive = element.getAsJsonPrimitive();
                    if (primitive.isNumber()) {
                        // Try to get as int first, then as double
                        double d = primitive.getAsDouble();
                        if (d == Math.floor(d) && d < Integer.MAX_VALUE) {
                            params.put(key, (int) d);
                        } else {
                            params.put(key, d);
                        }
                    } else if (primitive.isBoolean()) {
                        params.put(key, primitive.getAsBoolean());
                    } else {
                        params.put(key, primitive.getAsString());
                    }
                } else {
                    // For arrays/objects, store as string
                    params.put(key, element.toString());
                }
            } catch (Exception e) {
                // Skip problematic parameters
            }
        }

        return params;
    }

    /**
     * Create an UNKNOWN_ACTION response with an error message.
     */
    private AIResponse createUnknownResponse(String reason) {
        AIResponse response = new AIResponse();
        response.setAction(AIAction.UNKNOWN_ACTION);
        response.setConfidence(0.0);
        Map<String, Object> params = new HashMap<>();
        params.put("error", reason);
        response.setParameters(params);
        return response;
    }
}
