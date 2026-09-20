package com.commitrisk.service;

import com.commitrisk.ai.AIAnalysisService;
import com.commitrisk.analyzer.DetectedFactor;
import com.commitrisk.analyzer.RiskAnalysisResult;
import com.commitrisk.analyzer.RiskAnalyzer;
import com.commitrisk.dto.*;
import com.commitrisk.exception.AnalysisNotFoundException;
import com.commitrisk.model.CommitAnalysis;
import com.commitrisk.model.RecommendedTest;
import com.commitrisk.model.RiskFactor;
import com.commitrisk.model.RiskLevelEntity;
import com.commitrisk.parser.DiffParser;
import com.commitrisk.parser.ParsedDiff;
import com.commitrisk.repository.CommitAnalysisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Coordinates the full analysis pipeline:
 * raw diff -> DiffParser -> RiskAnalyzer -> AIAnalysisService -> persistence -> response DTO.
 */
@Service
public class CommitAnalysisService {

    private final DiffParser diffParser;
    private final RiskAnalyzer riskAnalyzer;
    private final AIAnalysisService aiAnalysisService;
    private final CommitAnalysisRepository repository;

    public CommitAnalysisService(DiffParser diffParser,
                                  RiskAnalyzer riskAnalyzer,
                                  AIAnalysisService aiAnalysisService,
                                  CommitAnalysisRepository repository) {
        this.diffParser = diffParser;
        this.riskAnalyzer = riskAnalyzer;
        this.aiAnalysisService = aiAnalysisService;
        this.repository = repository;
    }

    @Transactional
    public AnalyzeResponse analyze(AnalyzeRequest request) {
        ParsedDiff parsedDiff = diffParser.parse(request.getDiff());
        RiskAnalysisResult riskResult = riskAnalyzer.analyze(parsedDiff);
        AiAnalysisResult aiResult = aiAnalysisService.explain(parsedDiff, riskResult, request.getCommitMessage());

        CommitAnalysis entity = buildEntity(request, parsedDiff, riskResult, aiResult);
        CommitAnalysis saved = repository.save(entity);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AnalysisSummaryDto> getHistory() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(a -> new AnalysisSummaryDto(
                        a.getId(), a.getCommitMessage(), a.getRiskScore(),
                        a.getRiskLevel().name(), a.getFilesChanged(), a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AnalyzeResponse getById(Long id) {
        CommitAnalysis entity = repository.findById(id)
                .orElseThrow(() -> new AnalysisNotFoundException(id));
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        List<CommitAnalysis> all = repository.findAllByOrderByCreatedAtDesc();

        stats.setTotalAnalyses(all.size());
        stats.setHighRiskCommits(repository.countHighRiskCommits());
        Double avg = repository.averageRiskScore();
        stats.setAverageRiskScore(avg == null ? 0.0 : Math.round(avg * 10.0) / 10.0);

        if (!all.isEmpty()) {
            CommitAnalysis latest = all.get(0);
            stats.setLatestAnalysis(new AnalysisSummaryDto(
                    latest.getId(), latest.getCommitMessage(), latest.getRiskScore(),
                    latest.getRiskLevel().name(), latest.getFilesChanged(), latest.getCreatedAt()));
        }

        return stats;
    }

    // ---- mapping helpers ----

    private CommitAnalysis buildEntity(AnalyzeRequest request, ParsedDiff parsedDiff,
                                        RiskAnalysisResult riskResult, AiAnalysisResult aiResult) {
        CommitAnalysis entity = new CommitAnalysis();
        entity.setCommitMessage(request.getCommitMessage());
        entity.setRiskScore(riskResult.getRiskScore());
        entity.setRiskLevel(RiskLevelEntity.valueOf(riskResult.getRiskLevel().name()));
        entity.setFilesChanged(parsedDiff.getFilesChangedCount());
        entity.setLinesAdded(parsedDiff.getTotalLinesAdded());
        entity.setLinesDeleted(parsedDiff.getTotalLinesDeleted());
        entity.setAiSummary(aiResult.getSummary());
        entity.setAiExplanation(String.join("\n", aiResult.getPotentialRisks()));
        entity.setAiGenerated(aiResult.isAiGenerated());

        for (DetectedFactor factor : riskResult.getFactors()) {
            RiskFactor rf = new RiskFactor();
            rf.setFactorName(factor.getName());
            rf.setDescription(factor.getDescription());
            rf.setImpact(factor.getImpact());
            entity.addRiskFactor(rf);
        }

        for (String testDescription : aiResult.getRecommendedTests()) {
            RecommendedTest test = new RecommendedTest();
            test.setTestDescription(testDescription);
            test.setPriority(derivePriority(riskResult));
            entity.addRecommendedTest(test);
        }

        return entity;
    }

    private RecommendedTest.Priority derivePriority(RiskAnalysisResult riskResult) {
        return switch (riskResult.getRiskLevel()) {
            case CRITICAL, HIGH -> RecommendedTest.Priority.HIGH;
            case MEDIUM -> RecommendedTest.Priority.MEDIUM;
            default -> RecommendedTest.Priority.LOW;
        };
    }

    private AnalyzeResponse toResponse(CommitAnalysis entity) {
        AnalyzeResponse response = new AnalyzeResponse();
        response.setId(entity.getId());
        response.setCommitMessage(entity.getCommitMessage());
        response.setRiskScore(entity.getRiskScore());
        response.setRiskLevel(entity.getRiskLevel().name());
        response.setFilesChanged(entity.getFilesChanged());
        response.setLinesAdded(entity.getLinesAdded());
        response.setLinesDeleted(entity.getLinesDeleted());
        response.setAiSummary(entity.getAiSummary());
        response.setAiExplanation(entity.getAiExplanation());
        response.setAiGenerated(entity.isAiGenerated());
        response.setCreatedAt(entity.getCreatedAt());

        response.setRiskFactors(entity.getRiskFactors().stream()
                .map(rf -> new RiskFactorDto(rf.getFactorName(), rf.getDescription(), rf.getImpact()))
                .collect(Collectors.toList()));

        response.setPotentialRisks(entity.getAiExplanation() == null || entity.getAiExplanation().isBlank()
                ? List.of()
                : List.of(entity.getAiExplanation().split("\n")));

        response.setRecommendedTests(entity.getRecommendedTests().stream()
                .map(t -> new RecommendedTestDto(t.getTestDescription(), t.getPriority().name()))
                .collect(Collectors.toList()));

        return response;
    }
}
