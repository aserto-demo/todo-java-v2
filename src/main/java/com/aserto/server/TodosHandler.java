package com.aserto.server;

import com.aserto.AuthorizerClient;
import com.aserto.DirectoryClient;
import com.aserto.authorizer.v2.api.IdentityType;
import com.aserto.dao.Todo;
import com.aserto.model.*;
import com.aserto.store.TodoStore;
import com.aserto.store.UserStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Value;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.aserto.directory.common.v2.Object;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TodosHandler implements HttpHandler {
    private static final String ALLOWED = "allowed";
    private Authorizer authorizer;
    private UserStore userStore;
    private TodoStore todoStore;
    private ObjectMapper objectMapper;

    public TodosHandler(AuthorizerClient authzClient, DirectoryClient directoryClient, TodoStore todoStore) {
        authorizer = new Authorizer(authzClient);
        userStore = new UserStore(directoryClient);
        this.todoStore = todoStore;
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // For cors
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        switch (exchange.getRequestMethod().toUpperCase()) {
            case "GET":
                getTodos(exchange);
                break;
            case "OPTIONS":
                setOptions(exchange);
                break;
            case "POST":
                postTodos(exchange);
                break;
            case "PUT":
                putTodos(exchange);
                break;
            case "DELETE":
                deleteTodos(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
        }

        exchange.close();
    }

    private void getTodos(HttpExchange exchange) throws IOException {
        String jwtToken = Utils.extractJwt(exchange);
        IdentityCtx identityCtx = new IdentityCtx(jwtToken, IdentityType.IDENTITY_TYPE_JWT);
        PolicyCtx policyCtx = new PolicyCtx("todo", "todo", "todoApp.GET.todos", new String[]{ALLOWED});

        boolean allowed = authorizer.isAllowed(identityCtx, policyCtx);
        if (!allowed) {
            exchange.sendResponseHeaders(403, 0);
            return;
        }

        String response = objectMapper.writeValueAsString(todoStore.getTodos());

        exchange.sendResponseHeaders(200, response.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private void setOptions(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, GET, PATCH, OPTIONS, DELETE, PUT");
        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "3600");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, authorization");
        exchange.sendResponseHeaders(204, -1);
    }

    private void postTodos(HttpExchange exchange) throws IOException {
        String jwtToken = Utils.extractJwt(exchange);
        IdentityCtx identityCtx = new IdentityCtx(jwtToken, IdentityType.IDENTITY_TYPE_JWT);
        PolicyCtx policyCtx = new PolicyCtx("todo", "todo", "todoApp.POST.todos", new String[]{ALLOWED});

        boolean allowed = authorizer.isAllowed(identityCtx, policyCtx);
        if (!allowed) {
            exchange.sendResponseHeaders(403, 0);
            return;
        }

        User user = getUserFromJwt(jwtToken);
        String value = getResponseBody(exchange);
        Todo todo = objectMapper.readValue(value, Todo.class);
        todo.setOwnerID(user.getKey());

        todoStore.saveTodo(todo);

        String response = "{\"msg\":\"Todo created\"}";

        exchange.sendResponseHeaders(200, response.length());

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private User getUserFromJwt(String jwtToken) throws JsonProcessingException {
        JwtDecoder jwtDecoder = new JwtDecoder(jwtToken);
        String payload = jwtDecoder.decodePayload();
        Jwt jwt = objectMapper.readValue(payload, Jwt.class);

        Object userObject = userStore.getUserByKey(jwt.getSub());
        Map<String, Value> userProperties = userObject.getProperties().getFieldsMap();

        return new User(userObject.getKey(),userObject.getDisplayName(), userProperties.get("email").getStringValue(), userProperties.get("picture").getStringValue());
    }

    private String getResponseBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        int b;
        StringBuilder buf = new StringBuilder(512);
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }

        br.close();
        isr.close();

        return buf.toString();
    }

    private void putTodos(HttpExchange exchange) throws IOException {
        String jwtToken = Utils.extractJwt(exchange);
        IdentityCtx identityCtx = new IdentityCtx(jwtToken, IdentityType.IDENTITY_TYPE_JWT);
        PolicyCtx policyCtx = new PolicyCtx("todo", "todo", "todoApp.PUT.todos.__id", new String[]{ALLOWED});
        String personalId = extractPersonalId(exchange.getRequestURI().toString());
        Map<String, Value> resourceCtx = java.util.Map.of("personalId", Value.newBuilder().setStringValue(personalId).build());

        boolean allowed = authorizer.isAllowed(identityCtx, policyCtx, resourceCtx);
        if (!allowed) {
            exchange.sendResponseHeaders(403, 0);
            return;
        }

        String value = getResponseBody(exchange);
        Todo todo = objectMapper.readValue(value, Todo.class);
        todoStore.updateTodoById(todo.getId(), todo);

        String response = "{\"msg\":\"Todo updated\"}";

        exchange.sendResponseHeaders(200, response.length());

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private void deleteTodos(HttpExchange exchange) throws IOException {
        String jwtToken = Utils.extractJwt(exchange);
        IdentityCtx identityCtx = new IdentityCtx(jwtToken, IdentityType.IDENTITY_TYPE_JWT);
        PolicyCtx policyCtx = new PolicyCtx("todo", "todo", "todoApp.PUT.todos.__id", new String[]{ALLOWED});
        String personalId = extractPersonalId(exchange.getRequestURI().toString());
        Map<String, Value> resourceCtx = java.util.Map.of("personalId", Value.newBuilder().setStringValue(personalId).build());

        boolean allowed = authorizer.isAllowed(identityCtx, policyCtx, resourceCtx);
        if (!allowed) {
            exchange.sendResponseHeaders(403, 0);
            return;
        }

        String value = getResponseBody(exchange);
        Todo todo = objectMapper.readValue(value, Todo.class);
        todoStore.deleteTodoById(todo.getId());

        String response = "{\"msg\":\"Todo deleted\"}";

        exchange.sendResponseHeaders(200, response.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private String extractPersonalId(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
 }
