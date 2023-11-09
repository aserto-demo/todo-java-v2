package com.aserto.config;

import com.aserto.authroizer.mapper.resource.PathParamsResourceMapper;
import com.aserto.authroizer.mapper.resource.ResourceMapper;
import com.aserto.authroizer.mapper.resource.ResourceMapperError;
import com.aserto.model.Todo;
import com.aserto.store.TodoRepository;
import com.google.protobuf.Value;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Component
public class TodoOwnerResourceMapper implements ResourceMapper {
    private final TodoRepository todoRepository;
    private final PathParamsResourceMapper pathParamsResourceMapper;

    public TodoOwnerResourceMapper(TodoRepository todoRepository, RequestMappingHandlerMapping handlerMapping) {
        this.todoRepository = todoRepository;
        this.pathParamsResourceMapper = new PathParamsResourceMapper(handlerMapping);
    }

    @Override
    public Map<String, Value> getResource(HttpServletRequest request) throws ResourceMapperError {
        Map<String, Value> params = pathParamsResourceMapper.getResource(request);
        if (params.get("id") == null) {
            return Collections.emptyMap();
        }

        String todoId = params.get("id").getStringValue();

        if (!todoId.isEmpty()) {
            Todo todo = todoRepository.findById(todoId).orElseThrow();
            String ownerId = todo.getOwnerID();
            return Map.of("ownerID", Value.newBuilder().setStringValue(ownerId).build());
        } else {
            return Collections.emptyMap();
        }
    }
}
