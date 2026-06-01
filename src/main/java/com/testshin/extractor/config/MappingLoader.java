package com.testshin.extractor.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public final class MappingLoader {

    private final ObjectMapper mapper;

    public MappingLoader() {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Map<String, ExtractionConfig> load(Path mappingFile) throws IOException {
        return mapper.readValue(
                mappingFile.toFile(),
                new TypeReference<Map<String, ExtractionConfig>>() {
                }
        );
    }
}
