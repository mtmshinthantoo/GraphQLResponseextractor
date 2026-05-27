package com.testshin.extractor.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.testshin.extractor.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpandHandler {

    public static List<Map<String, String>> expandObjectArray(
            List<Map<String, String>> currentRows,
            JsonNode arrayNode,
            Map<String, String> mappings,
            String rootKey) {

        List<Map<String, String>> expanded = new ArrayList<>();

        for (Map<String, String> row : currentRows) {

            for (JsonNode item : arrayNode) {

                Map<String, String> newRow = new java.util.LinkedHashMap<>(row);

                // fill all fields corresponding to this array
                for (Map.Entry<String, String> mapping : mappings.entrySet()) {

                    String column = mapping.getKey();
                    String path = mapping.getValue();

                    if (path.startsWith(rootKey + ".")) {

                        JsonNode value = JsonUtils.extractFromItem(item, path);
                        newRow.put(column, JsonUtils.safeText(value));
                    }
                }

                expanded.add(newRow);
            }
        }

        return expanded;
    }
}