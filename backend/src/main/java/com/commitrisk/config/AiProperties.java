package com.commitrisk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds AI provider settings from environment variables / application
 * properties. No API key is ever hardcoded - it is always supplied via
 * the environment (see .env.example).
 *
 * application.properties maps:
 *   ai.api-key      -> ${AI_API_KEY:}
 *   ai.api-url      -> ${AI_API_URL:https://api.anthropic.com/v1/messages}
 *   ai.model        -> ${AI_MODEL:claude-sonnet-4-6}
 *   ai.provider     -> ${AI_PROVIDER:anthropic}
 *   ai.enabled      -> ${AI_ENABLED:true}
 *   ai.timeout-ms   -> ${AI_TIMEOUT_MS:15000}
 */
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private String apiKey;
    private String apiUrl;
    private String model;
    private String provider;
    private boolean enabled;
    private long timeoutMs;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public boolean isConfigured() {
        return enabled && apiKey != null && !apiKey.isBlank();
    }
}
