package com.commitrisk.model;

/**
 * Persistence-layer mirror of {@link com.commitrisk.analyzer.RiskLevel}.
 * Kept separate so the JPA model doesn't force a dependency direction
 * between the persistence and analysis packages.
 */
public enum RiskLevelEntity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
