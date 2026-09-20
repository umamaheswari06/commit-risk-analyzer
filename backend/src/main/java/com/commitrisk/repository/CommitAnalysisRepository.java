package com.commitrisk.repository;

import com.commitrisk.model.CommitAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitAnalysisRepository extends JpaRepository<CommitAnalysis, Long> {

    List<CommitAnalysis> findAllByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query(
            "select count(c) from CommitAnalysis c where c.riskLevel = com.commitrisk.model.RiskLevelEntity.HIGH " +
            "or c.riskLevel = com.commitrisk.model.RiskLevelEntity.CRITICAL")
    long countHighRiskCommits();

    @org.springframework.data.jpa.repository.Query("select avg(c.riskScore) from CommitAnalysis c")
    Double averageRiskScore();
}
