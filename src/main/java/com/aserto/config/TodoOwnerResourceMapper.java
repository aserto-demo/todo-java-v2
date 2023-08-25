package com.aserto.config;

import com.aserto.authroizer.mapper.resource.PathParamsResourceMapper;
import com.aserto.authroizer.mapper.resource.ResourceMapper;
import com.aserto.model.Todo;
import com.aserto.store.TodoStore;
import com.google.protobuf.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class TodoOwnerResourceMapper implements ResourceMapper {
    private final TodoStore todoStore;
    private final PathParamsResourceMapper pathParamsResourceMapper;

    public TodoOwnerResourceMapper(TodoStore todoStore, RequestMappingHandlerMapping handlerMapping) {
        this.todoStore = todoStore;
        this.pathParamsResourceMapper = new PathParamsResourceMapper(handlerMapping);
    }

    @Override
    public Map<String, Value> getResource(HttpServletRequest request) {
        Map<String, Value> params = pathParamsResourceMapper.getResource(request);
        if (params.get("id") == null) {
            return Collections.emptyMap();
        }

        String todoId = params.get("id").getStringValue();

        if (!todoId.isEmpty()) {
            Todo todo = todoStore.getTodo(todoId);
            String ownerId = todo.getOwnerID();
            return Map.of("ownerID", Value.newBuilder().setStringValue(ownerId).build());
        } else {
            return Collections.emptyMap();
        }
    }
}
