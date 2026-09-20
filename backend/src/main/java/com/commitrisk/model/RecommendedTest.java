package com.commitrisk.model;

import jakarta.persistence.*;

@Entity
@Table(name = "recommended_test")
public class RecommendedTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private CommitAnalysis analysis;

    @Column(length = 1000)
    private String testDescription;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    public enum Priority { LOW, MEDIUM, HIGH }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CommitAnalysis getAnalysis() { return analysis; }
    public void setAnalysis(CommitAnalysis analysis) { this.analysis = analysis; }

    public String getTestDescription() { return testDescription; }
    public void setTestDescription(String testDescription) { this.testDescription = testDescription; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}
