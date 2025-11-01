package ru.aston.GatewayApi.utils;

public class ErrorResponse {

    private final String error;
    private final String timestamp;

    public ErrorResponse(String error) {
        this.error = error;
        this.timestamp = String.valueOf(System.currentTimeMillis());
    }

    public String getError() {
        return error;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
