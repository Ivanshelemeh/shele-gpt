package com.example.shelegpt.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Role {

    USER("user") {
        @Override
        Message getMessage(String message) {
            return new UserMessage(message);
        }
    },

    ASSISTANT("assistant") {
        @Override
        Message getMessage(String promt) {
            return new AssistantMessage(promt);
        }
    },

    SYSTEM("system") {
        @Override
        Message getMessage(String promt) {
            return new SystemMessage(promt);
        }
    };

    private final String roleNam;

    public static Role getRole(String roleName) {
        return Arrays.stream(Role.values()).filter(role1 -> role1.roleNam.equals(roleName))
                     .findFirst()
                     .orElseThrow();
    }

    abstract Message getMessage(String promt);
}
