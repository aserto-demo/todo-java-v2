package com.aserto.store;

import com.aserto.model.Todo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TodoRepository extends CrudRepository<Todo, String> {
    Optional<Todo> findById(String id);
}
