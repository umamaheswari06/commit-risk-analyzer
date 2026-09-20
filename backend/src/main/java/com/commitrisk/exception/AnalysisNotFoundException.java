package com.commitrisk.exception;

public class AnalysisNotFoundException extends RuntimeException {
    public AnalysisNotFoundException(Long id) {
        super("No analysis found with id " + id);
    }
}
