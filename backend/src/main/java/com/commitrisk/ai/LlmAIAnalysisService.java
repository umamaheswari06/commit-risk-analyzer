package com.commitrisk.ai;

import com.commitrisk.analyzer.DetectedFactor;
import com.commitrisk.analyzer.RiskAnalysisResult;
import com.commitrisk.config.AiProperties;
import com.commitrisk.dto.AiAnalysisResult;
import com.commitrisk.parser.FileChange;
import com.commitrisk.parser.ParsedDiff;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Calls an LLM (Anthropic Messages API by default, configurable via
 * AI_API_URL/AI_MODEL/AI_PROVIDER) to turn the rule-based risk result into
 * a human-readable explanation plus targeted test recommendations.
 *
 * The provider is fully swappable through configuration, and any failure
 * (missing key, network error, malformed response) transparently falls
 * back to {@link RuleBasedExplanationGenerator} so the API never breaks.
 */
@Service
public class LlmAIAnalysisService implements AIAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(LlmAIAnalysisService.class);
    private static final Pattern JSON_BLOCK = Pattern.compile("\\{.*\\}", Pattern.DOTALL);

    private final RestTemplate restTemplate;
    private final AiProperties aiProperties;
    private final RuleBasedExplanationGenerator fallbackGenerator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LlmAIAnalysisService(RestTemplate restTemplate,
                                 AiProperties aiProperties,
                                 RuleBasedExplanationGenerator fallbackGenerator) {
        this.restTemplate = restTemplate;
        this.aiProperties = aiProperties;
        this.fallbackGenerator = fallbackGenerator;
    }

    @Override
    public AiAnalysisResult explain(ParsedDiff diff, RiskAnalysisResult riskResult, String commitMessage) {
        if (!aiProperties.isConfigured()) {
            log.info("AI provider not configured (AI_API_KEY missing or AI_ENABLED=false); using rule-based explanation.");
            return fallbackGenerator.generate(diff, riskResult);
        }

        try {
            String prompt = buildPrompt(diff, riskResult, commitMessage);
            String rawResponse = callProvider(prompt);
            AiAnalysisResult parsed = parseResponse(rawResponse);
            parsed.setAiGenerated(true);
            return parsed;
        } catch (Exception ex) {
            log.warn("AI analysis failed ({}); falling back to rule-based explanation.", ex.getMessage());
            return fallbackGenerator.generate(diff, riskResult);
        }
    }

    private String buildPrompt(ParsedDiff diff, RiskAnalysisResult riskResult, String commitMessage) {
        String factorList = riskResult.getFactors().stream()
                .map(f -> "- " + f.getName() + " (+" + f.getImpact() + "): " + f.getDescription())
                .collect(Collectors.joining("\n"));

        String fileList = diff.getFileChanges().stream()
                .map(FileChange::getFilePath)
                .collect(Collectors.joining(", "));

        // Keep the diff excerpt bounded so we don't blow past model context limits.
        String diffExcerpt = diff.getFileChanges().stream()
                .map(FileChange::getFullContent)
                .collect(Collectors.joining("\n"));
        if (diffExcerpt.length() > 6000) {
            diffExcerpt = diffExcerpt.substring(0, 6000) + "\n...[truncated]";
        }

        return """
                You are assisting a code review tool. Analyze the following commit and \
                return ONLY a valid JSON object (no markdown fences, no preamble) with \
                exactly these fields:
                {
                  "summary": "one or two sentence summary of what the commit changes",
                  "potentialRisks": ["short specific risk statement", "..."],
                  "recommendedTests": ["short specific test to run", "..."]
                }

                Commit message: %s
                Files changed: %s
                Lines added: %d, Lines deleted: %d
                Rule-based risk score: %d/100 (%s)
                Detected risk factors:
                %s

                Relevant diff content (added/removed lines, truncated):
                %s

                Focus potentialRisks and recommendedTests specifically on what changed - \
                do not give generic software advice. Do not claim to have found actual bugs; \
                describe risk of the change, not certainty of a defect.
                """.formatted(
                commitMessage == null || commitMessage.isBlank() ? "(not provided)" : commitMessage,
                fileList, diff.getTotalLinesAdded(), diff.getTotalLinesDeleted(),
                riskResult.getRiskScore(), riskResult.getRiskLevel(),
                factorList.isBlank() ? "(none detected)" : factorList,
                diffExcerpt);
    }

    private String callProvider(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", aiProperties.getApiKey());
        headers.set("anthropic-version", "2023-06-01");
        // Also set Authorization for providers that expect a Bearer token instead.
        headers.setBearerAuth(aiProperties.getApiKey());

        Map<String, Object> body = Map.of(
                "model", aiProperties.getModel(),
                "max_tokens", 1000,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(aiProperties.getApiUrl(), request, String.class);
        return response.getBody();
    }

    private AiAnalysisResult parseResponse(String rawResponse) throws Exception {
        JsonNode root = objectMapper.readTree(rawResponse);

        // Anthropic Messages API shape: { "content": [ { "type": "text", "text": "..." } ] }
        String text = null;
        if (root.has("content") && root.get("content").isArray()) {
            StringBuilder sb = new StringBuilder();
            for (JsonNode block : root.get("content")) {
                if (block.has("text")) {
                    sb.append(block.get("text").asText());
                }
            }
            text = sb.toString();
        } else if (root.has("choices")) {
            // OpenAI-compatible shape fallback
            text = root.at("/choices/0/message/content").asText();
        }

        if (text == null || text.isBlank()) {
            throw new IllegalStateException("Empty or unrecognized AI provider response");
        }

        Matcher matcher = JSON_BLOCK.matcher(text);
        String jsonPayload = matcher.find() ? matcher.group() : text;

        JsonNode parsed = objectMapper.readTree(jsonPayload);
        String summary = parsed.path("summary").asText("");
        List<String> risks = toStringList(parsed.path("potentialRisks"));
        List<String> tests = toStringList(parsed.path("recommendedTests"));

        return new AiAnalysisResult(summary, risks, tests, true);
    }

    private List<String> toStringList(JsonNode arrayNode) {
        List<String> result = new ArrayList<>();
        if (arrayNode.isArray()) {
            arrayNode.forEach(n -> result.add(n.asText()));
        }
        return result;
    }
}
