package com.commitrisk.dto;

import jakarta.validation.constraints.NotBlank;

public class AnalyzeRequest {

    @NotBlank(message = "diff must not be empty")
    private String diff;

    /** Optional - shown in history for readability. */
    private String commitMessage;

    public String getDiff() { return diff; }
    public void setDiff(String diff) { this.diff = diff; }

    public String getCommitMessage() { return commitMessage; }
    public void setCommitMessage(String commitMessage) { this.commitMessage = commitMessage; }
}
