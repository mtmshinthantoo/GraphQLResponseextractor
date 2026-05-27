package com.testshin.extractor.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.testshin.extractor.util.JsonUtils;

import java.util.List;
import java.util.Map;

public class ValueHandler {

    public static void applyValue(
            List<Map<String, String>> rows,
            String column,
            JsonNode node) {

        String value = JsonUtils.safeText(node);

        for (Map<String, String> row : rows) {
            row.put(column, value);
        }
    }
}