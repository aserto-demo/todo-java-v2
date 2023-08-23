package com.aserto.controller;

import com.aserto.model.Todo;
import com.aserto.directory.common.v2.Object;
import com.aserto.dto.Response;
import com.aserto.model.Jwt;
import com.aserto.server.JwtDecoder;
import com.aserto.store.TodoStore;
import com.aserto.store.UserStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

@RestController
public class TodosController {
    private final TodoStore todoStore;
    private final UserStore userStore;

    private final ObjectMapper objectMapper;

    public TodosController(UserStore userStore, ObjectMapper objectMapper) {
        this.todoStore = new TodoStore();
        this.userStore =  userStore;
        this. objectMapper = objectMapper;
    }

    @CrossOrigin
    @GetMapping("/todos")
    public Todo[] getTodos() {
        return todoStore.getTodos();
    }

    @CrossOrigin
    @PostMapping("/todos")
    public Response postTodo(@RequestHeader("Authorization") String jwtAuth,
                             @RequestBody Todo todo) throws JsonProcessingException {
        String[] authTokens = jwtAuth.split(" ");
        String jwtToken = authTokens[authTokens.length - 1];
        String userKey = getUserKeyFromJwt(jwtToken);

        todo.setOwnerID(userKey);
        todoStore.saveTodo(todo);

        return new Response("Todo created");
    }

    @CrossOrigin
    @PutMapping("/todos/{id}")
    public Response putTodo(@PathVariable String id,
                          @RequestBody Todo requestTodo) {
        // TODO add authz checks
        Todo todo = todoStore.getTodo(id);
        todoStore.updateTodoById(todo.getId(), requestTodo);

        return new Response("Todo updated");
    }

    @CrossOrigin
    @DeleteMapping("/todos/{id}")
    public Response deleteTodo(@PathVariable String id) {
        Todo todo = todoStore.getTodo(id);
        todoStore.deleteTodoById(todo.getId());

        return new Response("Todo deleted");
    }

    private String getUserKeyFromJwt(String jwtToken) throws JsonProcessingException {
        JwtDecoder jwtDecoder = new JwtDecoder(jwtToken);
        String payload = jwtDecoder.decodePayload();
        Jwt jwt = objectMapper.readValue(payload, Jwt.class);
        Object userObject = userStore.getUserBySub(jwt.getSub());

        return userObject.getKey();
    }
}
