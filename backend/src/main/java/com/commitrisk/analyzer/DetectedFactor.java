package com.commitrisk.analyzer;

/**
 * A single contributor to the overall risk score, e.g.
 * "Security-related changes" (+20), with a human-readable description
 * of why it was triggered.
 */
public class DetectedFactor {

    private final String name;
    private final String description;
    private final int impact;

    public DetectedFactor(String name, String description, int impact) {
        this.name = name;
        this.description = description;
        this.impact = impact;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImpact() {
        return impact;
    }
}
