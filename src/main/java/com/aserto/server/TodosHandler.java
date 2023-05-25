package com.aserto.server;

import com.aserto.dao.Todo;
import com.aserto.store.TodoStore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class TodosHandler implements HttpHandler {
    private TodoStore todoStore;
    private ObjectMapper objectMapper;

    public TodosHandler(TodoStore todoStore) {
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
        String value = getResponseBody(exchange);
        Todo todo = objectMapper.readValue(value, Todo.class);

        if (todo.getId() == null) {
            todo.setId(UUID.randomUUID().toString());
        }

        todoStore.saveTodo(todo);

        String response = "{\"msg\":\"Todo created\"}";

        exchange.sendResponseHeaders(200, response.length());

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
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
 }
