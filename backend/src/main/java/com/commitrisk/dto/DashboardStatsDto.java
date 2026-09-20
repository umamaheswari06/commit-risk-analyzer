package com.commitrisk.dto;

public class DashboardStatsDto {

    private long totalAnalyses;
    private long highRiskCommits;
    private double averageRiskScore;
    private AnalysisSummaryDto latestAnalysis;

    public long getTotalAnalyses() { return totalAnalyses; }
    public void setTotalAnalyses(long totalAnalyses) { this.totalAnalyses = totalAnalyses; }

    public long getHighRiskCommits() { return highRiskCommits; }
    public void setHighRiskCommits(long highRiskCommits) { this.highRiskCommits = highRiskCommits; }

    public double getAverageRiskScore() { return averageRiskScore; }
    public void setAverageRiskScore(double averageRiskScore) { this.averageRiskScore = averageRiskScore; }

    public AnalysisSummaryDto getLatestAnalysis() { return latestAnalysis; }
    public void setLatestAnalysis(AnalysisSummaryDto latestAnalysis) { this.latestAnalysis = latestAnalysis; }
}
