package com.example.shelegpt;

import com.example.shelegpt.repo.ChatRepository;
import com.example.shelegpt.service.impl.PostgresChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SheleGptApplication {

    @Autowired
    private ChatRepository chatRepository;

    @Bean
    public ChatClient createChatClient(ChatClient.Builder builder) {
        return builder.defaultAdvisors(getAdvisor()).build();
    }

    private Advisor getAdvisor() {
        return MessageChatMemoryAdvisor.builder(getChatMemory()).build();
    }

    private ChatMemory getChatMemory() {
        return PostgresChatMemory.builder()
                                 .maxMessages(3)
                                 .chatRepository(chatRepository)
                                 .build();

    }

    public static void main(String[] args) {
        SpringApplication.run(SheleGptApplication.class, args);
    }

}
