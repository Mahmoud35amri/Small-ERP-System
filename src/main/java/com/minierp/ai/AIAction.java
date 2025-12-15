package com.minierp.ai;

/**
 * Enumeration of all allowed AI actions.
 * The AI agent may ONLY return one of these actions.
 */
public enum AIAction {
    GET_TODAY_SALES,
    GET_MONTHLY_SALES,
    GET_LOW_STOCK,
    GET_PENDING_ORDERS,
    CREATE_ORDER,
    CREATE_INVOICE,
    UNKNOWN_ACTION;

    /**
     * Parse action from string, defaults to UNKNOWN_ACTION if not recognized.
     */
    public static AIAction fromString(String action) {
        if (action == null || action.isBlank()) {
            return UNKNOWN_ACTION;
        }
        try {
            return AIAction.valueOf(action.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return UNKNOWN_ACTION;
        }
    }
}
