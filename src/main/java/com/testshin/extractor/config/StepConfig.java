package com.testshin.extractor.config;

import java.util.ArrayList;
import java.util.List;

public class StepConfig {

    private static final String DEFAULT_SEPARATOR = ", ";

    private String type;
    private String column;
    private String path;
    private String separator = DEFAULT_SEPARATOR;
    private Boolean keepWhenEmpty = true;

    private String leftArray;
    private String leftValuePath = "";
    private String leftColumn;
    private String rightArray;
    private String rightKey;

    private List<FieldMapping> fields = new ArrayList<>();
    private List<FieldMapping> outputFields = new ArrayList<>();

    public StepConfig() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSeparator() {
        return separator == null ? DEFAULT_SEPARATOR : separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public boolean isKeepWhenEmpty() {
        return keepWhenEmpty == null || keepWhenEmpty;
    }

    public void setKeepWhenEmpty(Boolean keepWhenEmpty) {
        this.keepWhenEmpty = keepWhenEmpty;
    }

    public String getLeftArray() {
        return leftArray;
    }

    public void setLeftArray(String leftArray) {
        this.leftArray = leftArray;
    }

    public String getLeftValuePath() {
        return leftValuePath == null ? "" : leftValuePath;
    }

    public void setLeftValuePath(String leftValuePath) {
        this.leftValuePath = leftValuePath;
    }

    public String getLeftColumn() {
        return leftColumn;
    }

    public void setLeftColumn(String leftColumn) {
        this.leftColumn = leftColumn;
    }

    public boolean hasLeftColumn() {
        return leftColumn != null && !leftColumn.isBlank();
    }

    public String getRightArray() {
        return rightArray;
    }

    public void setRightArray(String rightArray) {
        this.rightArray = rightArray;
    }

    public String getRightKey() {
        return rightKey;
    }

    public void setRightKey(String rightKey) {
        this.rightKey = rightKey;
    }

    public List<FieldMapping> getFields() {
        return fields == null ? List.of() : fields;
    }

    public void setFields(List<FieldMapping> fields) {
        this.fields = fields;
    }

    public List<FieldMapping> getOutputFields() {
        return outputFields == null ? List.of() : outputFields;
    }

    public void setOutputFields(List<FieldMapping> outputFields) {
        this.outputFields = outputFields;
    }
}
