package com.aserto.config;

import com.aserto.authroizer.mapper.policy.NoMatchingMappingException;
import com.aserto.authroizer.mapper.resource.ResourceMapper;
import com.aserto.authroizer.mapper.resource.ResourceMapperError;
import com.aserto.directory.common.v2.Object;
import com.aserto.store.UserStore;
import com.google.protobuf.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TodoOwnerResourceMapper implements ResourceMapper {
    private final UserStore userStore;
    private final RequestMappingHandlerMapping handlerMapping;

    public TodoOwnerResourceMapper(UserStore userStore, RequestMappingHandlerMapping handlerMapping) {
        this.userStore = userStore;
        this.handlerMapping = handlerMapping;
    }

    @Override
    public Map<String, Value> getResource(HttpServletRequest request) throws ResourceMapperError {
        Map<String, String> params;
        try {
            params = extractParams(request);
        } catch (NoMatchingMappingException e) {
            throw new ResourceMapperError(e);
        }

        String idFromRequest = params.get("id");

        Object user = userStore.getUserByKey(idFromRequest);
        String id = user.getId();

        return Map.of("ownerID", Value.newBuilder().setStringValue(id).build());
    }

    public Map<String, String> extractParams(HttpServletRequest request) throws NoMatchingMappingException {
        String uri = request.getRequestURI();
        AntPathMatcher apm = new AntPathMatcher();
        String pattern = "";
        for (Map.Entry<RequestMappingInfo, HandlerMethod> mappingInfo : this.handlerMapping.getHandlerMethods().entrySet()) {
            PathPatternsRequestCondition pathPatternsCondition = mappingInfo.getKey().getPathPatternsCondition();
            if (pathPatternsCondition == null) {
                continue;
            }

            pattern = pathPatternsCondition.getPatterns().iterator().next().getPatternString();
            if (apm.match(pattern, uri)) {
                Map<String, String> paramsMapping = new HashMap<>();
                String[] pathParams = getPathParams(pattern);
                for (String param : pathParams) {
                    String paramValue = apm.extractUriTemplateVariables(pattern, uri).get(param);
                    paramsMapping.put(param, paramValue);
                }

                return paramsMapping;
            }
        }

        throw new NoMatchingMappingException("Uri" + uri + "does not match any mapping");
    }

    public String[] getPathParams(String uri) {
        List<String> params = new ArrayList<>();
        String[] tokens = uri.split("/");
        for (String token : tokens) {
            if (token.startsWith("{") && token.endsWith("}")) {
                params.add(token.substring(1, token.length() - 1));
            }
        }

        return params.toArray(new String[0]);
    }
}
