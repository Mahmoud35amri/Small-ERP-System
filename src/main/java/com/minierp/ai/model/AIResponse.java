package com.minierp.ai.model;

import com.minierp.ai.AIAction;
import java.util.HashMap;
import java.util.Map;

/**
 * Structured response from AI agent.
 * Matches the JSON schema:
 * {
 * "action": "ACTION_NAME",
 * "confidence": 0.0,
 * "parameters": { }
 * }
 */
public class AIResponse {
    private AIAction action;
    private double confidence;
    private Map<String, Object> parameters;

    public AIResponse() {
        this.action = AIAction.UNKNOWN_ACTION;
        this.confidence = 0.0;
        this.parameters = new HashMap<>();
    }

    public AIResponse(AIAction action, double confidence, Map<String, Object> parameters) {
        this.action = action;
        this.confidence = confidence;
        this.parameters = parameters != null ? parameters : new HashMap<>();
    }

    public AIAction getAction() {
        return action;
    }

    public void setAction(AIAction action) {
        this.action = action;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = Math.max(0.0, Math.min(1.0, confidence)); // Clamp to [0, 1]
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters != null ? parameters : new HashMap<>();
    }

    /**
     * Get a string parameter by key.
     */
    public String getStringParam(String key) {
        Object value = parameters.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Get an integer parameter by key.
     */
    public Integer getIntParam(String key) {
        Object value = parameters.get(key);
        if (value == null)
            return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Check if this is a valid, actionable response.
     */
    public boolean isValid() {
        return action != null && action != AIAction.UNKNOWN_ACTION && confidence >= 0.5;
    }

    @Override
    public String toString() {
        return "AIResponse{" +
                "action=" + action +
                ", confidence=" + confidence +
                ", parameters=" + parameters +
                '}';
    }
}
