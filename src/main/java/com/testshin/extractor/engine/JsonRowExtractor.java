package com.testshin.extractor.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testshin.extractor.config.ExtractionConfig;
import com.testshin.extractor.config.FieldMapping;
import com.testshin.extractor.config.StepConfig;
import com.testshin.extractor.config.StepType;
import com.testshin.extractor.exception.ExtractionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonRowExtractor {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Map<String, String>> extractRows(String json, ExtractionConfig config) {
        try {
            JsonNode responseRoot = mapper.readTree(json);
            JsonNode rootNode = JsonPath.resolve(responseRoot, config.getRootPath());
            List<Map<String, String>> result = new ArrayList<>();

            for (JsonNode rawItem : JsonPath.asList(rootNode)) {
                JsonNode item = JsonPath.resolve(rawItem, config.getItemPath());
                result.addAll(processItem(item, config));
            }

            return result;
        } catch (Exception exception) {
            throw new ExtractionException("Error extracting rows", exception);
        }
    }

    private List<Map<String, String>> processItem(JsonNode item, ExtractionConfig config) {
        List<Map<String, String>> rows = new ArrayList<>();
        rows.add(new LinkedHashMap<>());

        for (StepConfig step : config.getSteps()) {
            rows = applyStep(rows, item, step);
        }

        return rows;
    }

    private List<Map<String, String>> applyStep(
            List<Map<String, String>> rows,
            JsonNode item,
            StepConfig step) {

        return switch (StepType.from(step.getType())) {
            case VALUE -> applyValue(rows, step.getColumn(), JsonPath.resolve(item, step.getPath()));
            case FLATTEN -> applyFlatten(
                    rows,
                    step.getColumn(),
                    JsonPath.resolve(item, step.getPath()),
                    step.getSeparator()
            );
            case EXPAND -> applyExpand(
                    rows,
                    JsonPath.resolve(item, step.getPath()),
                    step.getFields(),
                    step.isKeepWhenEmpty()
            );
            case JOIN -> applyJoin(rows, item, step);
        };
    }

    private List<Map<String, String>> applyValue(
            List<Map<String, String>> rows,
            String column,
            JsonNode valueNode) {

        String value = JsonPath.text(valueNode);

        for (Map<String, String> row : rows) {
            row.put(column, value);
        }

        return rows;
    }

    private List<Map<String, String>> applyFlatten(
            List<Map<String, String>> rows,
            String column,
            JsonNode arrayNode,
            String separator) {

        List<String> values = new ArrayList<>();

        for (JsonNode item : JsonPath.asList(arrayNode)) {
            values.add(JsonPath.text(item));
        }

        String joined = String.join(separator, values);

        for (Map<String, String> row : rows) {
            row.put(column, joined);
        }

        return rows;
    }

    private List<Map<String, String>> applyExpand(
            List<Map<String, String>> rows,
            JsonNode arrayNode,
            List<FieldMapping> fields,
            boolean keepWhenEmpty) {

        List<JsonNode> items = JsonPath.asList(arrayNode);

        if (items.isEmpty()) {
            return keepWhenEmpty ? copyWithBlankFields(rows, fields) : List.of();
        }

        List<Map<String, String>> expandedRows = new ArrayList<>();

        for (Map<String, String> row : rows) {
            for (JsonNode item : items) {
                Map<String, String> expandedRow = new LinkedHashMap<>(row);
                putMappedFields(expandedRow, item, fields);
                expandedRows.add(expandedRow);
            }
        }

        return expandedRows;
    }

    private List<Map<String, String>> applyJoin(
            List<Map<String, String>> rows,
            JsonNode item,
            StepConfig step) {

        Map<String, JsonNode> lookup = buildLookup(
                JsonPath.resolve(item, step.getRightArray()),
                step.getRightKey()
        );
        List<JsonNode> leftItems = JsonPath.asList(JsonPath.resolve(item, step.getLeftArray()));

        if (leftItems.isEmpty()) {
            return step.isKeepWhenEmpty()
                    ? copyWithBlankJoinFields(rows, step)
                    : List.of();
        }

        List<Map<String, String>> joinedRows = new ArrayList<>();

        for (Map<String, String> row : rows) {
            for (JsonNode leftItem : leftItems) {
                Map<String, String> joinedRow = new LinkedHashMap<>(row);
                String leftKeyValue = JsonPath.text(JsonPath.resolve(leftItem, step.getLeftValuePath()));
                JsonNode matchedItem = lookup.get(leftKeyValue);

                if (step.hasLeftColumn()) {
                    joinedRow.put(step.getLeftColumn(), leftKeyValue);
                }

                if (matchedItem == null) {
                    putBlankFields(joinedRow, step.getOutputFields());
                } else {
                    putMappedFields(joinedRow, matchedItem, step.getOutputFields());
                }

                joinedRows.add(joinedRow);
            }
        }

        return joinedRows;
    }

    private Map<String, JsonNode> buildLookup(JsonNode arrayNode, String keyPath) {
        Map<String, JsonNode> lookup = new HashMap<>();

        for (JsonNode item : JsonPath.asList(arrayNode)) {
            lookup.put(JsonPath.text(JsonPath.resolve(item, keyPath)), item);
        }

        return lookup;
    }

    private void putMappedFields(
            Map<String, String> row,
            JsonNode source,
            List<FieldMapping> fields) {

        for (FieldMapping field : fields) {
            row.put(field.getColumn(), JsonPath.text(JsonPath.resolve(source, field.getPath())));
        }
    }

    private void putBlankFields(Map<String, String> row, List<FieldMapping> fields) {
        for (FieldMapping field : fields) {
            row.put(field.getColumn(), "");
        }
    }

    private List<Map<String, String>> copyWithBlankFields(
            List<Map<String, String>> rows,
            List<FieldMapping> fields) {

        List<Map<String, String>> copiedRows = new ArrayList<>();

        for (Map<String, String> row : rows) {
            Map<String, String> copiedRow = new LinkedHashMap<>(row);
            putBlankFields(copiedRow, fields);
            copiedRows.add(copiedRow);
        }

        return copiedRows;
    }

    private List<Map<String, String>> copyWithBlankJoinFields(
            List<Map<String, String>> rows,
            StepConfig step) {

        List<Map<String, String>> copiedRows = new ArrayList<>();

        for (Map<String, String> row : rows) {
            Map<String, String> copiedRow = new LinkedHashMap<>(row);

            if (step.hasLeftColumn()) {
                copiedRow.put(step.getLeftColumn(), "");
            }

            putBlankFields(copiedRow, step.getOutputFields());
            copiedRows.add(copiedRow);
        }

        return copiedRows;
    }
}
