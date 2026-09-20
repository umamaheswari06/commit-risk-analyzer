package com.commitrisk.analyzer;

/**
 * Project-defined risk classification bands. These thresholds (see
 * {@link RiskScoringConfig}) are heuristics tuned for this tool, not an
 * industry standard.
 */
public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
