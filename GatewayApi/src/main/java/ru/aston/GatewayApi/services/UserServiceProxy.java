package ru.aston.GatewayApi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.aston.GatewayApi.dtos.UserDTO;
import ru.aston.GatewayApi.exceptions.BadRequestException;
import ru.aston.GatewayApi.exceptions.ServiceUnavailableException;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Service
public class UserServiceProxy extends UserService {

    private static final AtomicInteger FAILURE_REQUESTS_COUNT = new AtomicInteger(0);
    private static final AtomicInteger HALF_OPEN_REQUESTS_COUNT = new AtomicInteger(0);
    private static final int TIMEOUT = 5000;
    private final AtomicLong CB_OPEN_TIME = new AtomicLong(0);
    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final int threshold = 2;
    private RestTemplate restTemplate;

    @Autowired
    public UserServiceProxy(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<?> executeWithCircuitBreaker(Supplier<ResponseEntity<?>> supplier) {
        updateCircuitBreakerState();
        return handleResponse(supplier);
    }

    @Override
    public ResponseEntity<?> getUser(Long id,String name,String email) {
        return executeWithCircuitBreaker(() -> super.getUser(id,name,email));
    }

    @Override
    public ResponseEntity<?> getUsers(Integer page,Integer size,String sort) {
        return executeWithCircuitBreaker(() -> super.getUsers(page,size,sort));
    }

    @Override
    public ResponseEntity<?> createUser(UserDTO userDTO) {
        return executeWithCircuitBreaker(() -> super.createUser(userDTO));
    }

    @Override
    public ResponseEntity<?> deleteUser(Long id) {
        return executeWithCircuitBreaker(() -> super.deleteUser(id));
    }

    @Override
    public ResponseEntity<?> updateUser(UserDTO userDTO) {
        return executeWithCircuitBreaker(() -> super.updateUser(userDTO));
    }

    private ResponseEntity<?> handleResponse(Supplier<ResponseEntity<?>> supplier) {
        if (state.get() == State.HALF_OPEN) return handleHalfOpen(supplier);
        else return handleClosed(supplier);
    }

    private ResponseEntity<?> handleClosed(Supplier<ResponseEntity<?>> supplier) {
        ResponseEntity<?> response = null;
        try {
            response = supplier.get();
        } catch (HttpClientErrorException e) {
            throw new BadRequestException(e.getResponseBodyAsString(),e.getStatusCode());
        } catch (HttpServerErrorException e) {
            FAILURE_REQUESTS_COUNT.incrementAndGet();
            throw new ServiceUnavailableException();
        }
        return response;
    }

    private ResponseEntity<?> handleHalfOpen(Supplier<ResponseEntity<?>> supplier) {
        ResponseEntity<?> response = null;
        try {
            response = supplier.get();

        } catch (HttpClientErrorException e) {
            throw new BadRequestException(e.getResponseBodyAsString(),e.getStatusCode());
        } catch (HttpServerErrorException e) {
            state.set(State.OPEN);
            CB_OPEN_TIME.set(System.currentTimeMillis());
            HALF_OPEN_REQUESTS_COUNT.set(0);
            throw new ServiceUnavailableException();
        }
        if (HALF_OPEN_REQUESTS_COUNT.get() == threshold) closeCB();
        return response;
    }

    private void updateCircuitBreakerState() {
        if (state.get() == State.CLOSED && FAILURE_REQUESTS_COUNT.get() >= threshold) {
            openCB();
        }
        if (state.get() == State.OPEN) {
            halfOpenCB();
        }
    }

    private void halfOpenCB() {
        long now = System.currentTimeMillis();
        if (now - CB_OPEN_TIME.get() > TIMEOUT) {
            state.set(State.HALF_OPEN);
        } else throw new ServiceUnavailableException();
    }

    private void openCB() {
        state.set(State.OPEN);
        CB_OPEN_TIME.set(System.currentTimeMillis());
    }

    private void closeCB() {
        state.set(State.CLOSED);
        FAILURE_REQUESTS_COUNT.set(0);
        HALF_OPEN_REQUESTS_COUNT.set(0);
    }

    enum State {
        OPEN,
        CLOSED,
        HALF_OPEN
    }
}
