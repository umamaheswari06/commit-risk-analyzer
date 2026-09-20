package com.commitrisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the AI-Powered Git Commit Risk Analyzer backend.
 *
 * The application exposes a REST API that accepts a raw Git diff,
 * parses it, runs it through a transparent rule-based risk engine,
 * optionally enriches the result with an AI-generated explanation,
 * and persists the analysis for later retrieval.
 */
@SpringBootApplication
public class CommitRiskAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommitRiskAnalyzerApplication.class, args);
    }
}
