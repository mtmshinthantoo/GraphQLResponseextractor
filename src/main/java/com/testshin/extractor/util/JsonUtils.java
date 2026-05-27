package com.testshin.extractor.util;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonUtils {

    //  Resolve nested path: "blog.id"
    public static JsonNode resolvePath(JsonNode node, String path) {

        JsonNode current = node;

        for (String part : path.split("\\.")) {
            current = current.path(part);
        }

        return current;
    }

    //  Extract value from array item: "customAttributes.key"
    public static JsonNode extractFromItem(JsonNode item, String fullPath) {

        String[] parts = fullPath.split("\\.");
        JsonNode value = item;

        // skip first part (array name)
        for (int i = 1; i < parts.length; i++) {
            value = value.path(parts[i]);
        }

        return value;
    }

    //  Safe string conversion
    public static String safeText(JsonNode node) {
        return node.isMissingNode() ? "" : node.asText("");
    }
}
