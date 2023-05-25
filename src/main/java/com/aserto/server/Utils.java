package com.aserto.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, String> getHeadersMap(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();
        Map<String, String> headersMap = new HashMap<>();

        headers.forEach((key, value) -> headersMap.put(key, value.get(0)));

        return headersMap;
    }
}
