package com.commitrisk.dto;

import java.util.List;

/**
 * Structured payload produced by the AI layer (or by the rule-based
 * fallback when the AI provider is unavailable/unconfigured).
 */
public class AiAnalysisResult {

    private String summary;
    private List<String> potentialRisks;
    private List<String> recommendedTests;
    private boolean aiGenerated;

    public AiAnalysisResult() {}

    public AiAnalysisResult(String summary, List<String> potentialRisks,
                             List<String> recommendedTests, boolean aiGenerated) {
        this.summary = summary;
        this.potentialRisks = potentialRisks;
        this.recommendedTests = recommendedTests;
        this.aiGenerated = aiGenerated;
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getPotentialRisks() { return potentialRisks; }
    public void setPotentialRisks(List<String> potentialRisks) { this.potentialRisks = potentialRisks; }

    public List<String> getRecommendedTests() { return recommendedTests; }
    public void setRecommendedTests(List<String> recommendedTests) { this.recommendedTests = recommendedTests; }

    public boolean isAiGenerated() { return aiGenerated; }
    public void setAiGenerated(boolean aiGenerated) { this.aiGenerated = aiGenerated; }
}
