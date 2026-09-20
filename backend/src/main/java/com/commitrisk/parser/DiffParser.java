package com.commitrisk.parser;

import com.commitrisk.exception.InvalidDiffException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a raw unified Git diff (the output of `git diff` / `git show`)
 * into a structured {@link ParsedDiff}.
 *
 * The parser is intentionally forgiving: it does not require every line
 * of a strictly well-formed diff, since real-world pasted diffs are often
 * missing index lines or trailing context. It relies on three anchors:
 *
 *   "diff --git a/<path> b/<path>"   -> starts a new file section
 *   "+++ b/<path>"                   -> confirms/refines the target path
 *   lines starting with '+' or '-'   -> counted as added/deleted
 *     (except the "+++"/"---" file headers themselves)
 */
@Component
public class DiffParser {

    private static final int MAX_DIFF_LENGTH = 2_000_000; // ~2MB guard rail
    private static final Pattern DIFF_HEADER =
            Pattern.compile("^diff --git a/(.+?) b/(.+)$");
    private static final Pattern NEW_FILE_PATH =
            Pattern.compile("^\\+\\+\\+ (?:b/)?(.+)$");
    private static final Pattern OLD_FILE_PATH =
            Pattern.compile("^--- (?:a/)?(.+)$");

    public ParsedDiff parse(String rawDiff) {
        validate(rawDiff);

        ParsedDiff parsedDiff = new ParsedDiff();
        FileChange current = null;

        String[] lines = rawDiff.split("\r?\n", -1);

        for (String line : lines) {
            Matcher headerMatch = DIFF_HEADER.matcher(line);
            if (headerMatch.matches()) {
                current = new FileChange(headerMatch.group(2).trim());
                parsedDiff.addFileChange(current);
                continue;
            }

            if (line.startsWith("Binary files")) {
                if (current != null) {
                    current.setBinary(true);
                }
                continue;
            }

            // Fallback: some pasted diffs omit the "diff --git" line entirely
            // and start straight from "--- a/file" / "+++ b/file".
            if (current == null) {
                Matcher oldPath = OLD_FILE_PATH.matcher(line);
                if (oldPath.matches() && !line.startsWith("--- /dev/null")) {
                    current = new FileChange(oldPath.group(1).trim());
                    parsedDiff.addFileChange(current);
                    continue;
                }
            }

            Matcher newPathMatch = NEW_FILE_PATH.matcher(line);
            if (newPathMatch.matches()) {
                String path = newPathMatch.group(1).trim();
                if (current == null) {
                    current = new FileChange(path);
                    parsedDiff.addFileChange(current);
                } else if (!path.equals("dev/null")) {
                    // refine path in case the diff --git header path differed
                    // (e.g. renames) - keep existing FileChange, path already set
                }
                continue;
            }

            if (line.startsWith("---")) {
                // old-file header, already handled above for the fallback case
                continue;
            }

            if (current == null) {
                // Content encountered before any recognizable file header;
                // ignore (e.g. commit message lines pasted above the diff).
                continue;
            }

            if (line.startsWith("+") && !line.startsWith("+++")) {
                current.addAddedLine(line.substring(1));
            } else if (line.startsWith("-") && !line.startsWith("---")) {
                current.addDeletedLine(line.substring(1));
            }
            // lines starting with '@@', ' ', or 'index ' are context/metadata
            // and intentionally not counted as changes.
        }

        if (parsedDiff.isEmpty()) {
            throw new InvalidDiffException(
                    "No recognizable file changes were found. Please paste a valid " +
                    "unified 'git diff' output (containing 'diff --git' or '+++ / ---' file headers).");
        }

        return parsedDiff;
    }

    private void validate(String rawDiff) {
        if (rawDiff == null || rawDiff.isBlank()) {
            throw new InvalidDiffException("The diff content must not be empty.");
        }
        if (rawDiff.length() > MAX_DIFF_LENGTH) {
            throw new InvalidDiffException(
                    "The diff is too large to analyze (max " + MAX_DIFF_LENGTH + " characters). " +
                    "Please split it into smaller commits.");
        }
    }
}
