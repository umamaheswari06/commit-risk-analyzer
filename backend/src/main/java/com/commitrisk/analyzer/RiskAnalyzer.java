package com.commitrisk.analyzer;

import com.commitrisk.parser.FileChange;
import com.commitrisk.parser.ParsedDiff;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.commitrisk.analyzer.PatternLibrary.*;

/**
 * Transparent, rule-based commit risk engine.
 *
 * The score is a simple additive model: each detected characteristic of
 * the diff contributes a fixed number of points (configurable via
 * {@link RiskScoringConfig}), the total is capped at 100, and the total
 * maps to a {@link RiskLevel} band. Every contributing factor is returned
 * alongside the score so the result is fully explainable - there is no
 * hidden ML model behind the number.
 *
 * This estimates *change risk* from measurable commit characteristics.
 * It does not - and cannot - detect actual bugs.
 */
@Component
public class RiskAnalyzer {

    private final RiskScoringConfig config;

    public RiskAnalyzer(RiskScoringConfig config) {
        this.config = config;
    }

    public RiskAnalysisResult analyze(ParsedDiff diff) {
        List<DetectedFactor> factors = new ArrayList<>();
        int score = 0;

        score += scoreChangeSize(diff, factors);
        score += scoreFileCount(diff, factors);
        score += scoreCategories(diff, factors);

        int normalizedScore = Math.min(100, Math.max(0, score));
        RiskLevel level = classify(normalizedScore);

        return new RiskAnalysisResult(normalizedScore, level, factors);
    }

    // ---- Individual scoring dimensions -------------------------------------

    private int scoreChangeSize(ParsedDiff diff, List<DetectedFactor> factors) {
        int totalLines = diff.getTotalChangedLines();
        if (totalLines >= config.getLargeChangeLineThreshold()) {
            factors.add(new DetectedFactor(
                    "Large code change",
                    "The commit changes " + totalLines + " lines, which is a large change size.",
                    config.getLargeChangePoints()));
            return config.getLargeChangePoints();
        } else if (totalLines >= config.getMediumChangeLineThreshold()) {
            factors.add(new DetectedFactor(
                    "Moderate code change",
                    "The commit changes " + totalLines + " lines, a moderate change size.",
                    config.getMediumChangePoints()));
            return config.getMediumChangePoints();
        }
        return 0;
    }

    private int scoreFileCount(ParsedDiff diff, List<DetectedFactor> factors) {
        int fileCount = diff.getFilesChangedCount();
        if (fileCount >= config.getManyFilesThreshold()) {
            factors.add(new DetectedFactor(
                    "Many files touched",
                    fileCount + " files were modified in a single commit, increasing coordination risk.",
                    config.getManyFilesPoints()));
            return config.getManyFilesPoints();
        } else if (fileCount >= config.getSeveralFilesThreshold()) {
            factors.add(new DetectedFactor(
                    "Several files touched",
                    fileCount + " files were modified in a single commit.",
                    config.getSeveralFilesPoints()));
            return config.getSeveralFilesPoints();
        }
        return 0;
    }

    private int scoreCategories(ParsedDiff diff, List<DetectedFactor> factors) {
        boolean database = false, security = false, payment = false,
                api = false, exceptionHandling = false, configuration = false,
                sensitiveFile = false;

        for (FileChange fc : diff.getFileChanges()) {
            String pathLower = fc.getFilePath().toLowerCase();
            String contentLower = fc.getFullContent().toLowerCase();

            if (matchesAny(pathLower, SENSITIVE_FILENAME_KEYWORDS)) sensitiveFile = true;
            if (matchesAny(contentLower, DATABASE_KEYWORDS) || matchesAny(pathLower, List.of("repository", "dao"))) database = true;
            if (matchesAny(contentLower, SECURITY_KEYWORDS)) security = true;
            if (matchesAny(contentLower, PAYMENT_KEYWORDS)) payment = true;
            if (matchesAny(contentLower, API_KEYWORDS)) api = true;
            if (matchesAny(contentLower, EXCEPTION_KEYWORDS)) exceptionHandling = true;
            if (matchesFilename(pathLower, CONFIGURATION_FILENAMES)) configuration = true;
        }

        int total = 0;

        if (database) {
            factors.add(new DetectedFactor("Database modification",
                    "Changes touch SQL, repository, or ORM-related code.", config.getDatabasePoints()));
            total += config.getDatabasePoints();
        }
        if (security) {
            factors.add(new DetectedFactor("Security-related changes",
                    "Changes involve authentication, authorization, tokens, or credentials.", config.getSecurityPoints()));
            total += config.getSecurityPoints();
        }
        if (payment) {
            factors.add(new DetectedFactor("Payment/transaction logic",
                    "Changes involve payment, billing, or transaction handling.", config.getPaymentPoints()));
            total += config.getPaymentPoints();
        }
        if (api) {
            factors.add(new DetectedFactor("API/controller changes",
                    "Changes modify REST endpoints or controller logic.", config.getApiPoints()));
            total += config.getApiPoints();
        }
        if (exceptionHandling) {
            factors.add(new DetectedFactor("Exception handling changes",
                    "Changes modify try/catch/throw logic, which can alter error behavior.", config.getExceptionHandlingPoints()));
            total += config.getExceptionHandlingPoints();
        }
        if (configuration) {
            factors.add(new DetectedFactor("Configuration/dependency changes",
                    "Changes modify build files, environment config, or dependencies.", config.getConfigurationPoints()));
            total += config.getConfigurationPoints();
        }
        if (sensitiveFile && !security && !payment && !database) {
            // Only add this generic flag if a more specific category didn't already fire,
            // to avoid double-counting the same underlying concern.
            factors.add(new DetectedFactor("Sensitive file area",
                    "One or more changed files are in a sensitive area (auth, user, config, gateway).", config.getAuthPoints()));
            total += config.getAuthPoints();
        }

        return total;
    }

    private boolean matchesAny(String haystackLowerCase, List<String> keywords) {
        for (String keyword : keywords) {
            if (containsKeyword(haystackLowerCase, keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesFilename(String pathLower, List<String> filenames) {
        for (String name : filenames) {
            if (pathLower.endsWith(name) || pathLower.contains("/" + name)) {
                return true;
            }
        }
        return false;
    }

    private RiskLevel classify(int score) {
        if (score <= config.getLowMax()) return RiskLevel.LOW;
        if (score <= config.getMediumMax()) return RiskLevel.MEDIUM;
        if (score <= config.getHighMax()) return RiskLevel.HIGH;
        return RiskLevel.CRITICAL;
    }
}
