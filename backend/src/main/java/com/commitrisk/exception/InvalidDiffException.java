package com.commitrisk.exception;

/**
 * Thrown when the supplied text is empty, not a recognizable unified diff,
 * or exceeds the size the parser is willing to process.
 */
public class InvalidDiffException extends RuntimeException {
    public InvalidDiffException(String message) {
        super(message);
    }
}
