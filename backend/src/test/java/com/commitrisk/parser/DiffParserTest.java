package com.commitrisk.parser;

import com.commitrisk.exception.InvalidDiffException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiffParserTest {

    private final DiffParser parser = new DiffParser();

    @Test
    void parsesSingleFileDiffWithCorrectLineCounts() {
        String diff = """
                diff --git a/src/Calculator.java b/src/Calculator.java
                index 123..456 100644
                --- a/src/Calculator.java
                +++ b/src/Calculator.java
                @@ -10,5 +10,8 @@
                 public double add(double a, double b) {
                -    return a + b;
                +    double result = a + b;
                +    return result;
                 }
                """;

        ParsedDiff result = parser.parse(diff);

        assertEquals(1, result.getFilesChangedCount());
        assertEquals(2, result.getTotalLinesAdded());
        assertEquals(1, result.getTotalLinesDeleted());
        assertEquals("src/Calculator.java", result.getFileChanges().get(0).getFilePath());
    }

    @Test
    void parsesMultiFileDiff() {
        String diff = """
                diff --git a/A.java b/A.java
                --- a/A.java
                +++ b/A.java
                @@ -1,1 +1,2 @@
                +new line in A
                diff --git a/B.java b/B.java
                --- a/B.java
                +++ b/B.java
                @@ -1,2 +1,1 @@
                -removed line in B
                """;

        ParsedDiff result = parser.parse(diff);

        assertEquals(2, result.getFilesChangedCount());
        assertEquals(1, result.getTotalLinesAdded());
        assertEquals(1, result.getTotalLinesDeleted());
    }

    @Test
    void detectsFileExtension() {
        String diff = """
                diff --git a/pom.xml b/pom.xml
                --- a/pom.xml
                +++ b/pom.xml
                @@ -1,1 +1,1 @@
                +<version>2.0</version>
                """;

        ParsedDiff result = parser.parse(diff);
        assertEquals("xml", result.getFileChanges().get(0).getFileExtension());
    }

    @Test
    void throwsOnEmptyDiff() {
        assertThrows(InvalidDiffException.class, () -> parser.parse(""));
        assertThrows(InvalidDiffException.class, () -> parser.parse("   "));
        assertThrows(InvalidDiffException.class, () -> parser.parse(null));
    }

    @Test
    void throwsOnUnrecognizableContent() {
        assertThrows(InvalidDiffException.class, () -> parser.parse("this is just some random text"));
    }

    @Test
    void handlesFallbackFormatWithoutDiffGitHeader() {
        String diff = """
                --- a/README.md
                +++ b/README.md
                @@ -1,1 +1,2 @@
                +Added a new line to the readme
                """;

        ParsedDiff result = parser.parse(diff);
        assertEquals(1, result.getFilesChangedCount());
        assertEquals(1, result.getTotalLinesAdded());
    }
}
