package com.aserto.server;

import com.aserto.DirectoryClient;
import com.aserto.model.User;
import com.aserto.store.UserStore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Value;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.aserto.directory.common.v2.Object;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class UsersHandler implements HttpHandler {
    private UserStore userStore;
    private ObjectMapper objectMapper;

    public UsersHandler(DirectoryClient directoryClient) {
        userStore = new UserStore(directoryClient);
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // For cors
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        switch (exchange.getRequestMethod().toUpperCase()) {
            case "GET":
                getUsers(exchange);
                break;
            case "OPTIONS":
                setOptions(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
        }
    }

    private void getUsers(HttpExchange exchange) throws IOException {
        String personalId = extractPersonalId(exchange.getRequestURI().toString());

        Object directoryUser = userStore.getUserByKey(personalId);
        Map<String, Value> userProperties = directoryUser.getProperties().getFieldsMap();

        User user = new User(directoryUser.getKey(),directoryUser.getDisplayName(), userProperties.get("email").getStringValue(), userProperties.get("picture").getStringValue());
        String response = objectMapper.writeValueAsString(user);

        exchange.sendResponseHeaders(200, response.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }

    private void setOptions(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, GET, PATCH, OPTIONS, DELETE, PUT");
        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "3600");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, authorization");
        exchange.sendResponseHeaders(204, -1);
    }

    private String extractPersonalId(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}
