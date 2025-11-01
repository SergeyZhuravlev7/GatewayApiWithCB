package ru.aston.GatewayApi.exceptions;

import org.springframework.http.HttpStatusCode;

public class BadRequestException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public BadRequestException(String message,HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatus() {
        return statusCode;
    }
}
