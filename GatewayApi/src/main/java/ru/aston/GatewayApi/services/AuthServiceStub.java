package ru.aston.GatewayApi.services;

import org.springframework.stereotype.Service;

@Service
public class AuthServiceStub {

    public boolean authenticate() {
        //логика аутентификации
        //если запрос поступил от не аутентифицированного бросить исключение
        return true;
    }
}
