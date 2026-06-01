package com.testshin.extractor.config;

import java.util.Locale;

public enum StepType {
    VALUE,
    FLATTEN,
    EXPAND,
    JOIN;

    public static StepType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Mapping step type is required");
        }

        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported mapping step type: " + value, exception);
        }
    }
}
