package ru.aston.GatewayApi.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestParser {

    public String getPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
