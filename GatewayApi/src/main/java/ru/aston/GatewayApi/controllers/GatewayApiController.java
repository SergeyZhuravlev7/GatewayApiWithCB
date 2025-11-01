package ru.aston.GatewayApi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aston.GatewayApi.dtos.UserDTO;
import ru.aston.GatewayApi.services.AuthServiceStub;
import ru.aston.GatewayApi.services.SMSNotificationServiceStub;
import ru.aston.GatewayApi.services.UserServiceProxy;

@RestController
@RequestMapping ("/api-gateway")
public class GatewayApiController {

    private final UserServiceProxy userService;
    private final AuthServiceStub authServiceStub;
    private final SMSNotificationServiceStub smsNotificationServiceStub;

    public GatewayApiController(
            UserServiceProxy userService,
            AuthServiceStub authServiceStub,
            SMSNotificationServiceStub smsNotificationServiceStub) {
        this.userService = userService;
        this.authServiceStub = authServiceStub;
        this.smsNotificationServiceStub = smsNotificationServiceStub;
    }

    @GetMapping ("/user")
    public ResponseEntity<?> getUser(
            @RequestParam (required = false) Long id,
            @RequestParam (required = false) String name,
            @RequestParam (required = false) String email) {
        authServiceStub.authenticate();
        return userService.getUser(id,name,email);
    }

    @GetMapping ("/user/all")
    public ResponseEntity<?> getAllUsers(
            @RequestParam (required = false) Integer page,
            @RequestParam (required = false) Integer size,
            @RequestParam (required = false) String sort) {
        authServiceStub.authenticate();
        return userService.getUsers(page,size,sort);
    }

    @PostMapping ("/user")
    public ResponseEntity<?> createUser(@RequestBody (required = false) UserDTO userDTO) {
        ResponseEntity<?> userServiceResponse = userService.createUser(userDTO);
        if (userServiceResponse
                .getStatusCode()
                .is2xxSuccessful()) {
            smsNotificationServiceStub.sendNotification();
        }
        return userServiceResponse;
    }

    @DeleteMapping ("/user")
    public ResponseEntity<?> deleteUser(@RequestParam (required = false) Long id) {
        authServiceStub.authenticate();
        ResponseEntity<?> userServiceResponse = userService.deleteUser(id);
        if (userServiceResponse
                .getStatusCode()
                .is2xxSuccessful()) {
            smsNotificationServiceStub.sendNotification();
        }
        return userServiceResponse;
    }

    @PutMapping ("/user")
    public ResponseEntity<?> updateUser(@RequestBody (required = false) UserDTO userDTO) {
        authServiceStub.authenticate();
        ResponseEntity<?> userServiceResponse = userService.updateUser(userDTO);
        if (userServiceResponse
                .getStatusCode()
                .is2xxSuccessful()) {
            smsNotificationServiceStub.sendNotification();
        }
        return userServiceResponse;
    }
}
