package com.aserto.controller;

import com.aserto.directory.v3.UninitilizedClientException;
import com.aserto.model.Todo;
import com.aserto.directory.common.v3.Object;
import com.aserto.model.Response;
import com.aserto.model.Jwt;
import com.aserto.helpers.JwtDecoder;
import com.aserto.store.TodoRepository;
import com.aserto.store.UserStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class TodosController {
    private final TodoRepository todoRepository;
    private final UserStore userStore;
    private final ObjectMapper objectMapper;

    public TodosController(UserStore userStore, TodoRepository todoRepository, ObjectMapper objectMapper) {
        this.todoRepository = todoRepository;
        this.userStore =  userStore;
        this. objectMapper = objectMapper;
    }

    @CrossOrigin
    @GetMapping("/todos")
    public Iterable<Todo> getTodos() {
        return todoRepository.findAll();
    }

    @CrossOrigin
    @PostMapping("/todos")
    public Response postTodo(@RequestHeader("Authorization") String jwtAuth,
                             @RequestBody Todo todo) throws JsonProcessingException, UninitilizedClientException {
        String[] authTokens = jwtAuth.split(" ");
        String jwtToken = authTokens[authTokens.length - 1];
        String userKey = getUserKeyFromJwt(jwtToken);

        todo.setOwnerID(userKey);
        todoRepository.save(todo);

        return new Response("Todo created");
    }

    @CrossOrigin
    @PutMapping("/todos/{id}")
    public Response putTodo(@PathVariable String id,
                          @RequestBody Todo requestTodo) {
        Optional<Todo> todoOptional = todoRepository.findById(id);
        Todo todo = null;
        if (todoOptional.isPresent()) {
            todo = todoOptional.get();
        }
        else {
            return new Response("Todo not found");
        }

        todo.setCompleted(requestTodo.getCompleted());
        todoRepository.save(todo);

        return new Response("Todo updated");
    }

    @CrossOrigin
    @DeleteMapping("/todos/{id}")
    public Response deleteTodo(@PathVariable String id) {
        todoRepository.deleteById(id);

        return new Response("Todo deleted");
    }

    private String getUserKeyFromJwt(String jwtToken) throws JsonProcessingException, UninitilizedClientException {
        JwtDecoder jwtDecoder = new JwtDecoder(jwtToken);
        String payload = jwtDecoder.decodePayload();
        Jwt jwt = objectMapper.readValue(payload, Jwt.class);
        Object userObject = userStore.getUserBySub(jwt.getSub());

        return userObject.getId();
    }
}
