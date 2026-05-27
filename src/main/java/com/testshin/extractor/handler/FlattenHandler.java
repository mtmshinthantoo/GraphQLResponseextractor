package com.testshin.extractor.handler;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlattenHandler {

    public static void applyFlatten(
            List<Map<String, String>> rows,
            String column,
            JsonNode arrayNode) {

        List<String> values = new ArrayList<>();

        for (JsonNode item : arrayNode) {
            values.add(item.asText(""));
        }

        String joined = String.join(",", values);

        for (Map<String, String> row : rows) {
            row.put(column, joined);
        }
    }
}
