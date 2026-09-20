package com.commitrisk.ai;

import com.commitrisk.analyzer.RiskAnalysisResult;
import com.commitrisk.dto.AiAnalysisResult;
import com.commitrisk.parser.ParsedDiff;

/**
 * Abstraction over "whatever LLM we call to explain a commit's risk".
 * The rest of the application depends only on this interface, so
 * swapping providers (or adding a second one) never touches the
 * controller/service layer.
 */
public interface AIAnalysisService {

    /**
     * Produce a human-readable summary, potential risks, and recommended
     * tests for the given diff + rule-based risk result.
     *
     * Implementations MUST NOT throw on provider failure - they should
     * catch the error internally and return a sensible fallback, since
     * the rest of the pipeline must keep working even if the AI call fails.
     */
    AiAnalysisResult explain(ParsedDiff diff, RiskAnalysisResult riskResult, String commitMessage);
}
