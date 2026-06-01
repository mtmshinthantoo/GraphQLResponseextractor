package com.testshin.extractor;

import com.testshin.extractor.config.ExtractionConfig;
import com.testshin.extractor.config.MappingLoader;
import com.testshin.extractor.engine.JsonRowExtractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String DEFAULT_MAPPING_NAME = "OrderLineItems";
    private static final Path DEFAULT_MAPPING_FILE = Path.of("src/main/resources/mappings/export-mappings.json");
    private static final Path DEFAULT_RESPONSE_FILE = Path.of("src/main/resources/responses/order-items-response.json");

    public static void main(String[] args) throws Exception {
        String mappingName = args.length > 0 ? args[0] : DEFAULT_MAPPING_NAME;
        Path responseFile = args.length > 1 ? Path.of(args[1]) : DEFAULT_RESPONSE_FILE;
        Path mappingFile = args.length > 2 ? Path.of(args[2]) : DEFAULT_MAPPING_FILE;

        ExtractionConfig config = loadConfig(mappingName, mappingFile);
        String json = Files.readString(responseFile);
        List<Map<String, String>> rows = new JsonRowExtractor().extractRows(json, config);

        printRows(mappingName, rows);
    }

    private static ExtractionConfig loadConfig(String mappingName, Path mappingFile) throws IOException {
        Map<String, ExtractionConfig> mappings = new MappingLoader().load(mappingFile);
        ExtractionConfig config = mappings.get(mappingName);

        if (config == null) {
            throw new IllegalArgumentException("Mapping not found: " + mappingName);
        }

        return config;
    }

    private static void printRows(String mappingName, List<Map<String, String>> rows) {
        System.out.println("Mapping = " + mappingName);
        System.out.println("Rows Count = " + rows.size());

        for (Map<String, String> row : rows) {
            System.out.println(row);
        }
    }
}
