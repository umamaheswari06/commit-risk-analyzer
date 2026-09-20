package com.commitrisk.dto;

import java.time.LocalDateTime;

/** Lightweight projection used for the analysis history list (GET /api/analyses). */
public class AnalysisSummaryDto {

    private Long id;
    private String commitMessage;
    private int riskScore;
    private String riskLevel;
    private int filesChanged;
    private LocalDateTime createdAt;

    public AnalysisSummaryDto() {}

    public AnalysisSummaryDto(Long id, String commitMessage, int riskScore,
                               String riskLevel, int filesChanged, LocalDateTime createdAt) {
        this.id = id;
        this.commitMessage = commitMessage;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.filesChanged = filesChanged;
        this.createdAt = createdAt;
    }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
