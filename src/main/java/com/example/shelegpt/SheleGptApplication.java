package com.example.shelegpt;

import com.example.shelegpt.repo.ChatRepository;
import com.example.shelegpt.service.impl.PostgresChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SheleGptApplication {


    private static final PromptTemplate MY_CUSTOM_PROMT =
            new PromptTemplate(
                    "{query}\n\nКонтекстная информация приведена ниже, окруженная ---------------------\n\n---------------------\n{question_answer_context}\n---------------------\n\nОсновываясь на контексте и предоставленной истории, а не на предварительных знаниях,\nответьте на комментарий пользователя. Если ответа нет в контексте, сообщите\nпользователю, что вы не можете ответить на вопрос.\n");

    private static final Integer TOP_K = 5;

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private VectorStore vectorStore;

    @Bean
    public ChatClient createChatClient(ChatClient.Builder builder) {
        return builder.defaultAdvisors(
                              getHistoryAdvisor(),
                              getRagAdvisor())
                      .build();
    }

    private Advisor getHistoryAdvisor() {
        return MessageChatMemoryAdvisor.builder(getChatMemory()).build();
    }

    private Advisor getRagAdvisor() {
        return QuestionAnswerAdvisor.builder(vectorStore)
                                    .promptTemplate(MY_CUSTOM_PROMT)
                                    .searchRequest(SearchRequest.builder()
                                                                .similarityThreshold(0.1)
                                                                .topK(TOP_K)
                                                                .build())
                                    .build();
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
