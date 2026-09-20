package com.commitrisk.analyzer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * All "magic numbers" used by {@link RiskAnalyzer} live here, bound from
 * application.properties (prefix "risk.*"). Tuning the risk model never
 * requires touching Java code.
 */
@Component
@ConfigurationProperties(prefix = "risk")
public class RiskScoringConfig {

    // ---- Change size thresholds & points ----
    private int largeChangeLineThreshold = 300;
    private int mediumChangeLineThreshold = 100;
    private int largeChangePoints = 15;
    private int mediumChangePoints = 8;

    // ---- File count thresholds & points ----
    private int manyFilesThreshold = 6;
    private int severalFilesThreshold = 3;
    private int manyFilesPoints = 10;
    private int severalFilesPoints = 5;

    // ---- Category points ----
    private int databasePoints = 15;
    private int securityPoints = 20;
    private int paymentPoints = 20;
    private int apiPoints = 10;
    private int exceptionHandlingPoints = 10;
    private int configurationPoints = 10;
    private int authPoints = 15;

    // ---- Classification thresholds (upper bound of each band) ----
    private int lowMax = 30;
    private int mediumMax = 60;
    private int highMax = 80;
    // anything above highMax is CRITICAL

    // ---- Getters and setters (required for @ConfigurationProperties binding) ----

    public int getLargeChangeLineThreshold() { return largeChangeLineThreshold; }
    public void setLargeChangeLineThreshold(int v) { this.largeChangeLineThreshold = v; }

    public int getMediumChangeLineThreshold() { return mediumChangeLineThreshold; }
    public void setMediumChangeLineThreshold(int v) { this.mediumChangeLineThreshold = v; }

    public int getLargeChangePoints() { return largeChangePoints; }
    public void setLargeChangePoints(int v) { this.largeChangePoints = v; }

    public int getMediumChangePoints() { return mediumChangePoints; }
    public void setMediumChangePoints(int v) { this.mediumChangePoints = v; }

    public int getManyFilesThreshold() { return manyFilesThreshold; }
    public void setManyFilesThreshold(int v) { this.manyFilesThreshold = v; }

    public int getSeveralFilesThreshold() { return severalFilesThreshold; }
    public void setSeveralFilesThreshold(int v) { this.severalFilesThreshold = v; }

    public int getManyFilesPoints() { return manyFilesPoints; }
    public void setManyFilesPoints(int v) { this.manyFilesPoints = v; }

    public int getSeveralFilesPoints() { return severalFilesPoints; }
    public void setSeveralFilesPoints(int v) { this.severalFilesPoints = v; }

    public int getDatabasePoints() { return databasePoints; }
    public void setDatabasePoints(int v) { this.databasePoints = v; }

    public int getSecurityPoints() { return securityPoints; }
    public void setSecurityPoints(int v) { this.securityPoints = v; }

    public int getPaymentPoints() { return paymentPoints; }
    public void setPaymentPoints(int v) { this.paymentPoints = v; }

    public int getApiPoints() { return apiPoints; }
    public void setApiPoints(int v) { this.apiPoints = v; }

    public int getExceptionHandlingPoints() { return exceptionHandlingPoints; }
    public void setExceptionHandlingPoints(int v) { this.exceptionHandlingPoints = v; }

    public int getConfigurationPoints() { return configurationPoints; }
    public void setConfigurationPoints(int v) { this.configurationPoints = v; }

    public int getAuthPoints() { return authPoints; }
    public void setAuthPoints(int v) { this.authPoints = v; }

    public int getLowMax() { return lowMax; }
    public void setLowMax(int v) { this.lowMax = v; }

    public int getMediumMax() { return mediumMax; }
    public void setMediumMax(int v) { this.mediumMax = v; }

    public int getHighMax() { return highMax; }
    public void setHighMax(int v) { this.highMax = v; }
}
