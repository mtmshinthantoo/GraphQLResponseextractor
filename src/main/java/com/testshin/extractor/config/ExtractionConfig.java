package com.testshin.extractor.config;

import java.util.ArrayList;
import java.util.List;

public class ExtractionConfig {

    private String rootPath;
    private String itemPath = "";
    private List<StepConfig> steps = new ArrayList<>();

    public ExtractionConfig() {
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getItemPath() {
        return itemPath == null ? "" : itemPath;
    }

    public void setItemPath(String itemPath) {
        this.itemPath = itemPath;
    }

    public List<StepConfig> getSteps() {
        return steps == null ? List.of() : steps;
    }

    public void setSteps(List<StepConfig> steps) {
        this.steps = steps;
    }
}
