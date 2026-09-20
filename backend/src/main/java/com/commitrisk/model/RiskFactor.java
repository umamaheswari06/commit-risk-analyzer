package com.commitrisk.model;

import jakarta.persistence.*;

@Entity
@Table(name = "risk_factor")
public class RiskFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private CommitAnalysis analysis;

    private String factorName;

    @Column(length = 1000)
    private String description;

    private int impact;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CommitAnalysis getAnalysis() { return analysis; }
    public void setAnalysis(CommitAnalysis analysis) { this.analysis = analysis; }

    public String getFactorName() { return factorName; }
    public void setFactorName(String factorName) { this.factorName = factorName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getImpact() { return impact; }
    public void setImpact(int impact) { this.impact = impact; }
}
