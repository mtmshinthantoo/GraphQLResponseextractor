package com.testshin.extractor.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class GraphQLRowExtractor {

    public List<Map<String, String>> extractRows(
            String json,
            String rootPath,
            Map<String, String> mappings) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            System.out.println(rootNode);
            JsonNode rootArray = resolvePath(rootNode, rootPath);
            System.out.println(rootArray);
            if (!rootArray.isArray()) {
                throw new RuntimeException("Root path must point to an array");
            }

            List<Map<String, String>> result = new ArrayList<>();
            System.out.println(result);
            for (JsonNode edge : rootArray) {
                JsonNode node = edge.path("node");
                System.out.println("node\n");
                System.out.println(node);

                // Process one node into rows
                List<Map<String, String>> rows = processNode(node, mappings);

                result.addAll(rows);
                System.out.println(result);
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error extracting rows", e);
        }
    }

    //  Core engine (clean separation)
    private List<Map<String, String>> processNode(
            JsonNode node,
            Map<String, String> mappings) {

        // empty array
        List<Map<String, String>> currentRows = new ArrayList<>();
        currentRows.add(new LinkedHashMap<>());

        // to skip next record (prevent duplicate rows)
        boolean customAttributesProcessed = false;

        for (Map.Entry<String, String> mapping : mappings.entrySet()) {

            String column = mapping.getKey();
            String path = mapping.getValue();

            //  skip already processed grouped array
            if (path.startsWith("customAttributes.") && customAttributesProcessed) {
                continue;
            }

            String[] parts = path.split("\\.");
            String rootKey = parts[0];
            JsonNode firstNode = node.path(rootKey);
            System.out.println("firstNode:");
            System.out.println(firstNode);
            System.out.println("currentRows : " + currentRows);
            //  EXPANSION BLOCK (array of objects)
            if (isExpandableArray(rootKey, firstNode, customAttributesProcessed)) {

                currentRows = expandCustomAttributes(
                        currentRows,
                        firstNode,
                        mappings
                );

                customAttributesProcessed = true;
                continue;
            }

            //  NORMAL or ARRAY FLATTEN
            JsonNode valueNode = resolvePath(node, path);
            System.out.println("valueNode");
            System.out.println(valueNode);

            if (valueNode.isArray()) {
                applyFlatten(currentRows, column, valueNode);
            } else {
                applyValue(currentRows, column, valueNode);
            }
        }

        return currentRows;
    }

    //  Expand customAttributes safely (single-responsibility)
    private List<Map<String, String>> expandCustomAttributes(
            List<Map<String, String>> currentRows,
            JsonNode arrayNode,
            Map<String, String> mappings) {

        List<Map<String, String>> expanded = new ArrayList<>();

        for (Map<String, String> row : currentRows) {
            System.out.println();
            System.out.println("currentRows" + currentRows);

            for (JsonNode item : arrayNode) {

                Map<String, String> newRow = new LinkedHashMap<>(row);

                //  fill ALL related fields in ONE place
                for (Map.Entry<String, String> mapping : mappings.entrySet()) {

                    String column = mapping.getKey();
                    String path = mapping.getValue();

                    if (path.startsWith("customAttributes.")) {

                        JsonNode value = extractFromItem(item, path);
                        newRow.put(column, safeText(value));
                    }
                }

                expanded.add(newRow);
            }
        }

        return expanded;
    }

    //  Flatten array → "A,B,C"
    private void applyFlatten(
            List<Map<String, String>> rows,
            String column,
            JsonNode arrayNode) {

        List<String> values = new ArrayList<>();

        for (JsonNode item : arrayNode) {
            values.add(item.asText(""));
        }
        // string join "A","B","C"
        String joined = String.join(",", values);

        for (Map<String, String> row : rows) {
            row.put(column, joined);
        }
    }

    //  Apply single value
    private void applyValue(
            List<Map<String, String>> rows,
            String column,
            JsonNode node) {

        String value = safeText(node);

        for (Map<String, String> row : rows) {
            row.put(column, value);
        }
    }

    //  Safe path traversal (blog.id → node → blog → id)
    private JsonNode resolvePath(JsonNode node, String path) {

        JsonNode current = node;

        for (String part : path.split("\\.")) {
            current = current.path(part);
        }

        return current;
    }

    //  Extract value from object array items
    private JsonNode extractFromItem(JsonNode item, String fullPath) {

        String[] parts = fullPath.split("\\.");
        JsonNode value = item;

        for (int i = 1; i < parts.length; i++) {
            value = value.path(parts[i]);
        }

        return value;
    }

    //  To return null/empty string if there is no data
    private String safeText(JsonNode node) {
        return node.isMissingNode() ? "" : node.asText("");
    }

    // Expand condition declare
    private boolean isExpandableArray(
            String rootKey,
            JsonNode node,
            boolean processed) {

        return node.isArray()
                && rootKey.equals("customAttributes")
                && !processed;
    }
}