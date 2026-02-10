package com.example.shelegpt.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenUsageMonitor {

    @Value("${spring.ai.ollama.chat.options.max-tokens}")
    private int thresholdTokens;

    private final MeterRegistry meterRegistry;

    @EventListener
    public void onChatResponse(ChatResponse response) {
        Usage usage = response.getMetadata().getUsage();

        meterRegistry.counter("llm.tokens.input", "model", response.getMetadata().getModel())
                     .increment(usage.getPromptTokens());

        meterRegistry.counter("llm.tokens.output", "model", response.getMetadata().getModel())
                     .increment(usage.getCompletionTokens());

        int totalTokens = usage.getPromptTokens() + usage.getCompletionTokens();
        if (totalTokens > thresholdTokens) {
            log.warn(
                    "High tokens usage ={} tokens (input ={} , output={})",
                    totalTokens, usage.getPromptTokens(), usage.getCompletionTokens()
            );
        }

    }
}
