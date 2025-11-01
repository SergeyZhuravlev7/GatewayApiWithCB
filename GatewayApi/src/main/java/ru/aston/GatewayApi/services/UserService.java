package ru.aston.GatewayApi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.aston.GatewayApi.dtos.UserDTO;

import java.net.URI;

@Service
public class UserService {

    private final static String SUFFIX = "/user";
    private final RestTemplate restTemplate;
    @Value ("${service.url.user-service-discovery}")
    private String BASE_URL;

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> getUser(Long id,String name,String email) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + SUFFIX)
                .queryParam("id",id)
                .queryParam("name",name)
                .queryParam("email",email)
                .build()
                .toUri()
                ;

        return restTemplate.getForEntity(uri,String.class);
    }

    public ResponseEntity<?> getUsers(Integer page,Integer size,String sort) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + SUFFIX + "/all")
                .queryParam("page",page)
                .queryParam("size",size)
                .queryParam("sort",sort)
                .build()
                .toUri()
                ;

        return restTemplate.getForEntity(uri,String.class);
    }

    public ResponseEntity<?> createUser(UserDTO userDTO) {
        return restTemplate.postForEntity(BASE_URL + SUFFIX,userDTO,String.class);
    }

    public ResponseEntity<?> deleteUser(Long id) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + SUFFIX)
                .queryParam("id",id)
                .build()
                .toUri()
                ;

        return restTemplate.exchange(uri,HttpMethod.DELETE,null,String.class);
    }

    public ResponseEntity<?> updateUser(UserDTO userDTO) {
        return restTemplate.exchange(BASE_URL + SUFFIX,HttpMethod.PUT,new HttpEntity<>(userDTO),String.class);
    }
}

