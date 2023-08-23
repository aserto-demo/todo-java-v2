package com.aserto.controller;

import com.aserto.ChannelBuilder;
import com.aserto.DirectoryClient;
import com.aserto.EnvConfigLoader;
import com.aserto.directory.common.v2.Object;
import com.aserto.model.Jwt;
import com.aserto.model.User;
import com.aserto.server.JwtDecoder;
import com.aserto.store.UserStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Value;
import io.grpc.ManagedChannel;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.SSLException;
import java.util.Map;

@RestController
public class UserController {
    private final ObjectMapper objectMapper;
    private final UserStore userStore;

    public UserController() throws SSLException {
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // TODO Should read config from application.properties file ?
        EnvConfigLoader envCfgLoader = new EnvConfigLoader();
        ManagedChannel directoryChannel = new ChannelBuilder(envCfgLoader.getDirectoryConfig()).build();
        DirectoryClient directoryClient = new DirectoryClient(directoryChannel);
        userStore = new UserStore(directoryClient);
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
