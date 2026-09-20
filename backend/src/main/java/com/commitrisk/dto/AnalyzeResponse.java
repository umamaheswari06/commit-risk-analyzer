package com.commitrisk.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AnalyzeResponse {

    private Long id;
    private String commitMessage;
    private int riskScore;
    private String riskLevel;
    private int filesChanged;
    private int linesAdded;
    private int linesDeleted;
    private List<RiskFactorDto> riskFactors;
    private String aiSummary;
    private String aiExplanation;
    private List<String> potentialRisks;
    private List<RecommendedTestDto> recommendedTests;
    private boolean aiGenerated;
    private LocalDateTime createdAt;

    // ---- Getters / setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCommitMessage() { return commitMessage; }
    public void setCommitMessage(String commitMessage) { this.commitMessage = commitMessage; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public int getFilesChanged() { return filesChanged; }
    public void setFilesChanged(int filesChanged) { this.filesChanged = filesChanged; }

    public int getLinesAdded() { return linesAdded; }
    public void setLinesAdded(int linesAdded) { this.linesAdded = linesAdded; }

    public int getLinesDeleted() { return linesDeleted; }
    public void setLinesDeleted(int linesDeleted) { this.linesDeleted = linesDeleted; }

    public List<RiskFactorDto> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<RiskFactorDto> riskFactors) { this.riskFactors = riskFactors; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public String getAiExplanation() { return aiExplanation; }
    public void setAiExplanation(String aiExplanation) { this.aiExplanation = aiExplanation; }

    public List<String> getPotentialRisks() { return potentialRisks; }
    public void setPotentialRisks(List<String> potentialRisks) { this.potentialRisks = potentialRisks; }

    public List<RecommendedTestDto> getRecommendedTests() { return recommendedTests; }
    public void setRecommendedTests(List<RecommendedTestDto> recommendedTests) { this.recommendedTests = recommendedTests; }

    public boolean isAiGenerated() { return aiGenerated; }
    public void setAiGenerated(boolean aiGenerated) { this.aiGenerated = aiGenerated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
