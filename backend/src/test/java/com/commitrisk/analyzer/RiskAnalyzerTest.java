package com.commitrisk.analyzer;

import com.commitrisk.parser.DiffParser;
import com.commitrisk.parser.ParsedDiff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RiskAnalyzerTest {

    private RiskAnalyzer analyzer;
    private final DiffParser parser = new DiffParser();

    @BeforeEach
    void setUp() {
        analyzer = new RiskAnalyzer(new RiskScoringConfig());
    }

    @Test
    void classifiesSmallDocChangeAsLow() {
        String diff = """
                diff --git a/README.md b/README.md
                --- a/README.md
                +++ b/README.md
                @@ -1,1 +1,2 @@
                +Added a short note to the docs.
                """;

        ParsedDiff parsed = parser.parse(diff);
        RiskAnalysisResult result = analyzer.analyze(parsed);

        assertEquals(RiskLevel.LOW, result.getRiskLevel());
        assertTrue(result.getRiskScore() <= 30);
    }

    @Test
    void detectsDatabaseChangeAndRaisesScore() {
        String diff = """
                diff --git a/src/UserRepository.java b/src/UserRepository.java
                --- a/src/UserRepository.java
                +++ b/src/UserRepository.java
                @@ -1,3 +1,5 @@
                +@Query("SELECT u FROM User u WHERE u.active = true")
                +List<User> findActiveUsers();
                """;

        ParsedDiff parsed = parser.parse(diff);
        RiskAnalysisResult result = analyzer.analyze(parsed);

        assertTrue(result.getFactors().stream()
                .anyMatch(f -> f.getName().equals("Database modification")));
    }

    @Test
    void detectsSecurityAndPaymentChangesAsCritical() {
        String diff = """
                diff --git a/src/SecurityConfig.java b/src/SecurityConfig.java
                --- a/src/SecurityConfig.java
                +++ b/src/SecurityConfig.java
                @@ -1,10 +1,40 @@
                +public String generateJwtToken(String username, String password, String role) {
                +    String token = jwtProvider.generate(username, role);
                +    return token;
                +}
                +
                diff --git a/src/PaymentService.java b/src/PaymentService.java
                --- a/src/PaymentService.java
                +++ b/src/PaymentService.java
                @@ -1,5 +1,20 @@
                +public double processPayment(double amount) {
                +    double tax = amount * 0.18;
                +    chargeCard(amount + tax);
                +    return amount + tax;
                +}
                diff --git a/src/TransactionRepository.java b/src/TransactionRepository.java
                --- a/src/TransactionRepository.java
                +++ b/src/TransactionRepository.java
                @@ -1,2 +1,10 @@
                +@Query("UPDATE Transaction t SET t.status = 'DONE' WHERE t.id = :id")
                +void markDone(Long id);
                diff --git a/src/PaymentController.java b/src/PaymentController.java
                --- a/src/PaymentController.java
                +++ b/src/PaymentController.java
                @@ -1,2 +1,10 @@
                +@PostMapping("/api/payments")
                +public ResponseEntity<String> pay() { return ResponseEntity.ok("done"); }
                diff --git a/pom.xml b/pom.xml
                --- a/pom.xml
                +++ b/pom.xml
                @@ -1,1 +1,2 @@
                +<dependency>new-lib</dependency>
                diff --git a/src/Extra1.java b/src/Extra1.java
                --- a/src/Extra1.java
                +++ b/src/Extra1.java
                @@ -1,1 +1,2 @@
                +// extra file to push file count up
                diff --git a/src/Extra2.java b/src/Extra2.java
                --- a/src/Extra2.java
                +++ b/src/Extra2.java
                @@ -1,1 +1,2 @@
                +// extra file to push file count up
                """;

        ParsedDiff parsed = parser.parse(diff);
        RiskAnalysisResult result = analyzer.analyze(parsed);

        assertEquals(RiskLevel.CRITICAL, result.getRiskLevel());
        assertTrue(result.getRiskScore() > 80);
        assertTrue(result.getFactors().stream().anyMatch(f -> f.getName().equals("Security-related changes")));
        assertTrue(result.getFactors().stream().anyMatch(f -> f.getName().equals("Payment/transaction logic")));
    }

    @Test
    void scoreIsNeverAboveOneHundredOrBelowZero() {
        RiskScoringConfig config = new RiskScoringConfig();
        RiskAnalyzer strictAnalyzer = new RiskAnalyzer(config);

        String diff = """
                diff --git a/README.md b/README.md
                --- a/README.md
                +++ b/README.md
                @@ -1,1 +1,1 @@
                +typo fix
                """;
        RiskAnalysisResult result = strictAnalyzer.analyze(parser.parse(diff));
        assertTrue(result.getRiskScore() >= 0 && result.getRiskScore() <= 100);
    }
}
