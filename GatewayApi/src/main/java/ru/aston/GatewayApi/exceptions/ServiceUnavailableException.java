package ru.aston.GatewayApi.exceptions;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException() {
        super("Service temporally unavailable. Please try again later.");
    }
}
