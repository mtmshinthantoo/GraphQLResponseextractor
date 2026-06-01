package com.testshin.extractor.engine;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

final class JsonPath {

    private JsonPath() {
    }

    static JsonNode resolve(JsonNode node, String path) {
        if (node == null || path == null || path.isBlank()) {
            return node;
        }

        JsonNode current = node;

        for (String part : path.split("\\.")) {
            if (current == null || current.isMissingNode() || current.isNull()) {
                return current;
            }

            current = current.isArray() && part.matches("\\d+")
                    ? current.path(Integer.parseInt(part))
                    : current.path(part);
        }

        return current;
    }

    static List<JsonNode> asList(JsonNode node) {
        List<JsonNode> values = new ArrayList<>();

        if (node == null || node.isMissingNode() || node.isNull()) {
            return values;
        }

        if (node.isArray()) {
            node.forEach(values::add);
        } else {
            values.add(node);
        }

        return values;
    }

    static String text(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return "";
        }

        return node.isContainerNode() ? node.toString() : node.asText("");
    }
}
