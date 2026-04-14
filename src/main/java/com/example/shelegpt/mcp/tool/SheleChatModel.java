package com.example.shelegpt.mcp.tool;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.messages.MessageType;

public record SheleChatModel(
        @JsonPropertyDescription
        String inputMessage,
        @JsonPropertyDescription(value = "message type")
        MessageType messageType
) {
}
