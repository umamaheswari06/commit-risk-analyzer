package com.commitrisk.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * The structured result of parsing a raw Git diff: the list of files
 * touched and aggregate line-change counts.
 */
public class ParsedDiff {

    private final List<FileChange> fileChanges = new ArrayList<>();

    public void addFileChange(FileChange fileChange) {
        fileChanges.add(fileChange);
    }

    public List<FileChange> getFileChanges() {
        return fileChanges;
    }

    public int getFilesChangedCount() {
        return fileChanges.size();
    }

    public int getTotalLinesAdded() {
        return fileChanges.stream().mapToInt(FileChange::getLinesAdded).sum();
    }

    public int getTotalLinesDeleted() {
        return fileChanges.stream().mapToInt(FileChange::getLinesDeleted).sum();
    }

    public int getTotalChangedLines() {
        return getTotalLinesAdded() + getTotalLinesDeleted();
    }

    public boolean isEmpty() {
        return fileChanges.isEmpty();
    }
}
