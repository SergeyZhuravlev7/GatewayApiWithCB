package ru.aston.GatewayApi.dtos;

public class UserDTO {

    private String name;
    private String email;
    private Integer age;

    public UserDTO() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }
}