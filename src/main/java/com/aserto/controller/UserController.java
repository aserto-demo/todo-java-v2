package com.aserto.controller;

import com.aserto.directory.common.v2.Object;
import com.aserto.model.Jwt;
import com.aserto.model.User;
import com.aserto.helpers.JwtDecoder;
import com.aserto.store.UserStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {
    private final ObjectMapper objectMapper;
    private final UserStore userStore;

    public UserController(UserStore userStore, ObjectMapper objectMapper) {
        this.userStore = userStore;
        this.objectMapper = objectMapper;
    }

    @CrossOrigin
    @GetMapping("/users/{userID}")
    public User getUser(@PathVariable String userID,
                          @RequestHeader("Authorization") String jwtAuth) throws JsonProcessingException {
        String[] authTokens = jwtAuth.split(" ");
        String jwtToken = authTokens[authTokens.length - 1];

        return extractUser(jwtToken, userID);
    }

    private User extractUser(String jwtToken, String personalId) throws JsonProcessingException {
        JwtDecoder jwtDecoder = new JwtDecoder(jwtToken);
        String payload = jwtDecoder.decodePayload();
        Jwt jwt = objectMapper.readValue(payload, Jwt.class);

        Object directoryUser;
        if (jwt.getSub().equals(personalId)) {
            directoryUser = userStore.getUserBySub(personalId);
        } else {
            directoryUser = userStore.getUserByKey(personalId);
        }

        Map<String, Value> userProperties = directoryUser.getProperties().getFieldsMap();

        return new User(directoryUser.getKey(),
                directoryUser.getDisplayName(),
                userProperties.get("email").getStringValue(),
                userProperties.get("picture").getStringValue());
    }
}
