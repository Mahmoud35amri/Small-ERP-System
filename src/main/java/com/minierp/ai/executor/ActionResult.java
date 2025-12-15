package com.minierp.ai.executor;

import java.util.List;

public class ActionResult {

    private final boolean success;
    private final String message;
    private final Object data;
    private final String errorCode;

    private ActionResult(boolean success, String message, Object data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    public static ActionResult success(String message, Object data) {
        return new ActionResult(true, message, data, null);
    }

    public static ActionResult success(String message) {
        return new ActionResult(true, message, null, null);
    }

    public static ActionResult error(String message, String errorCode) {
        return new ActionResult(false, message, null, errorCode);
    }

    public static ActionResult error(String message) {
        return new ActionResult(false, message, null, "GENERAL_ERROR");
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    public <T> T getDataAs(Class<T> clazz) {
        if (data == null)
            return null;
        return (T) data;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getDataAsList() {
        if (data == null)
            return null;
        return (List<T>) data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "ActionResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}
