package com.commitrisk.analyzer;

import java.util.List;

/**
 * Output of {@link RiskAnalyzer}: the normalized score, its classification,
 * and the list of individual factors that contributed to it.
 */
public class RiskAnalysisResult {

    private final int riskScore;
    private final RiskLevel riskLevel;
    private final List<DetectedFactor> factors;

    public RiskAnalysisResult(int riskScore, RiskLevel riskLevel, List<DetectedFactor> factors) {
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.factors = factors;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public List<DetectedFactor> getFactors() {
        return factors;
    }
}
