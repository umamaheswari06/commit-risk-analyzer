package com.commitrisk.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Persisted record of one commit analysis run: the summary metrics plus
 * its associated risk factors and recommended tests.
 */
@Entity
@Table(name = "commit_analysis")
public class CommitAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String commitMessage;

    private int riskScore;

    @Enumerated(EnumType.STRING)
    private RiskLevelEntity riskLevel;

    private int filesChanged;
    private int linesAdded;
    private int linesDeleted;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String aiExplanation;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    private boolean aiGenerated;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RiskFactor> riskFactors = new ArrayList<>();

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RecommendedTest> recommendedTests = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void addRiskFactor(RiskFactor factor) {
        factor.setAnalysis(this);
        riskFactors.add(factor);
    }

    public void addRecommendedTest(RecommendedTest test) {
        test.setAnalysis(this);
        recommendedTests.add(test);
    }

    // ---- Getters / setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCommitMessage() { return commitMessage; }
    public void setCommitMessage(String commitMessage) { this.commitMessage = commitMessage; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public RiskLevelEntity getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevelEntity riskLevel) { this.riskLevel = riskLevel; }

    public int getFilesChanged() { return filesChanged; }
    public void setFilesChanged(int filesChanged) { this.filesChanged = filesChanged; }

    public int getLinesAdded() { return linesAdded; }
    public void setLinesAdded(int linesAdded) { this.linesAdded = linesAdded; }

    public int getLinesDeleted() { return linesDeleted; }
    public void setLinesDeleted(int linesDeleted) { this.linesDeleted = linesDeleted; }

    public String getAiExplanation() { return aiExplanation; }
    public void setAiExplanation(String aiExplanation) { this.aiExplanation = aiExplanation; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public boolean isAiGenerated() { return aiGenerated; }
    public void setAiGenerated(boolean aiGenerated) { this.aiGenerated = aiGenerated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<RiskFactor> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<RiskFactor> riskFactors) { this.riskFactors = riskFactors; }

    public List<RecommendedTest> getRecommendedTests() { return recommendedTests; }
    public void setRecommendedTests(List<RecommendedTest> recommendedTests) { this.recommendedTests = recommendedTests; }
}
