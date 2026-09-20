package com.commitrisk.ai;

import com.commitrisk.analyzer.DetectedFactor;
import com.commitrisk.analyzer.RiskAnalysisResult;
import com.commitrisk.dto.AiAnalysisResult;
import com.commitrisk.parser.ParsedDiff;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Produces a deterministic, template-based explanation directly from the
 * rule-based risk factors. Used whenever the AI provider is disabled,
 * unconfigured, or fails - guaranteeing the pipeline always returns a
 * complete, useful result end-to-end without depending on an external API.
 */
@Component
public class RuleBasedExplanationGenerator {

    public AiAnalysisResult generate(ParsedDiff diff, RiskAnalysisResult riskResult) {
        String summary = buildSummary(diff, riskResult);
        List<String> risks = riskResult.getFactors().stream()
                .map(DetectedFactor::getDescription)
                .collect(Collectors.toList());
        if (risks.isEmpty()) {
            risks.add("No significant risk indicators were detected by the rule engine; " +
                    "this appears to be a routine, low-impact change.");
        }
        List<String> tests = suggestTests(riskResult);

        return new AiAnalysisResult(summary, risks, tests, false);
    }

    private String buildSummary(ParsedDiff diff, RiskAnalysisResult riskResult) {
        return String.format(
                "This commit changes %d file(s) with %d line(s) added and %d line(s) removed. " +
                "The rule-based engine estimates a risk score of %d/100 (%s), driven by %d detected factor(s).",
                diff.getFilesChangedCount(), diff.getTotalLinesAdded(), diff.getTotalLinesDeleted(),
                riskResult.getRiskScore(), riskResult.getRiskLevel(), riskResult.getFactors().size());
    }

    private List<String> suggestTests(RiskAnalysisResult riskResult) {
        List<String> tests = new ArrayList<>();
        for (DetectedFactor factor : riskResult.getFactors()) {
            switch (factor.getName()) {
                case "Database modification" -> {
                    tests.add("Run repository/DAO integration tests against a test database.");
                    tests.add("Verify data integrity after CREATE/UPDATE/DELETE operations.");
                }
                case "Security-related changes" -> {
                    tests.add("Test authentication with valid and invalid credentials.");
                    tests.add("Verify authorization rules for each affected role.");
                }
                case "Payment/transaction logic" -> {
                    tests.add("Test a successful end-to-end payment flow.");
                    tests.add("Test payment failure and rollback scenarios.");
                }
                case "API/controller changes" -> {
                    tests.add("Test each modified endpoint with valid and invalid request payloads.");
                }
                case "Exception handling changes" -> {
                    tests.add("Verify error responses are returned correctly for expected failure cases.");
                }
                case "Configuration/dependency changes" -> {
                    tests.add("Run a full application startup to confirm configuration/dependency changes don't break the build.");
                }
                case "Large code change", "Moderate code change" -> {
                    tests.add("Run the full regression test suite given the size of this change.");
                }
                case "Sensitive file area" -> {
                    tests.add("Manually review changes in this sensitive area with a second reviewer.");
                }
                default -> { /* no specific suggestion for this factor */ }
            }
        }
        if (tests.isEmpty()) {
            tests.add("Run standard unit tests for the modified files.");
        }
        return tests.stream().distinct().collect(Collectors.toList());
    }
}
