package com.commitrisk.parser;

/**
 * Represents the changes made to a single file within a Git diff.
 */
public class FileChange {

    private final String filePath;
    private final String fileExtension;
    private int linesAdded;
    private int linesDeleted;
    private final StringBuilder addedContent = new StringBuilder();
    private final StringBuilder removedContent = new StringBuilder();
    private boolean binary;

    public FileChange(String filePath) {
        this.filePath = filePath;
        this.fileExtension = extractExtension(filePath);
    }

    private static String extractExtension(String path) {
        if (path == null) return "";
        int dot = path.lastIndexOf('.');
        int slash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        if (dot > slash && dot != -1) {
            return path.substring(dot + 1).toLowerCase();
        }
        return "";
    }

    public void addAddedLine(String line) {
        linesAdded++;
        addedContent.append(line).append('\n');
    }

    public void addDeletedLine(String line) {
        linesDeleted++;
        removedContent.append(line).append('\n');
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public int getLinesAdded() {
        return linesAdded;
    }

    public int getLinesDeleted() {
        return linesDeleted;
    }

    public int getTotalChangedLines() {
        return linesAdded + linesDeleted;
    }

    public String getAddedContent() {
        return addedContent.toString();
    }

    public String getRemovedContent() {
        return removedContent.toString();
    }

    /** Combined added + removed content, used for keyword/pattern scanning. */
    public String getFullContent() {
        return addedContent.toString() + removedContent;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }
}
