package com.example.shelegpt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class McpService {

    private final ChatClient.Builder chatClientBuilder;
    private final ToolCallbackProvider toolCallbackProvider;
    private final ToolCompressor compressor;
    @Value("${spring.ai.anthropic.chat.options.max-tokens}")
    private int maxTokens;

    private final Map<String, Pattern> toolPatterns = Map.of(
            "filesystem", Pattern.compile("(?i)(файл|file|read|write|создай|открой|директор)"),
            "postgres", Pattern.compile("(?i)(sql|query|таблиц|database|бд|запрос|select)"),
            "github", Pattern.compile("(?i)(github|repo|commit|pull|issue|pr)")
    );

    public String chatMcp(@NonNull String inputMessage) {
        if (compressor.exceedsLimit(inputMessage, maxTokens)) {
            log.info("You tokens of message are over  limit={}", maxTokens);
            inputMessage = compressor.truncateToLimit(inputMessage, maxTokens);
        }

        ChatClient chatClient = chatClientBuilder.build();

        Set<String> requiredTools = requiredTool(inputMessage);

        var promt = chatClient.prompt().user(inputMessage);
        if (!requiredTools.isEmpty()) {
            List<ToolCallback> filteredTools = filteredTool(requiredTools);
            log.info("Using ={} tools = {}", filteredTools.size(), requiredTools);
            promt.tools(filteredTools);
        } else {
            log.info("No needed tools , save tokens");
        }
        return promt.call().content();

    }

    private Set<String> requiredTool(String message) {
        Set<String> tools = new HashSet<>();

        for (var entry : toolPatterns.entrySet()) {
            if (entry.getValue().matcher(message).find()) {
                tools.add(entry.getKey());
            }
        }
        return tools;

    }

    private List<ToolCallback> filteredTool(Set<String> requiredTools) {
        return Arrays.stream(toolCallbackProvider.getToolCallbacks())
                     .filter(tool -> {
                         String toolName = tool.getToolDefinition().name().toLowerCase();
                         return requiredTools.stream().anyMatch(
                                 toolName::contains
                         );
                     })
                     .toList();

    }
}
