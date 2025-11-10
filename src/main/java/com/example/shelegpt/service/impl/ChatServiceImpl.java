package com.example.shelegpt.service.impl;

import com.example.shelegpt.entity.ChatEntity;
import com.example.shelegpt.entity.ChatEntry;
import com.example.shelegpt.entity.Role;
import com.example.shelegpt.repo.ChatRepository;
import com.example.shelegpt.service.ChatService;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Objects;

@Service
public class ChatServiceImpl implements ChatService {

    private static final String SORTED_FIELD = "createdAt";
    private final ChatRepository chatRepository;
    private final ChatClient chatClient;

    public ChatServiceImpl(ChatRepository chatRepository, ChatClient chatClient) {
        this.chatRepository = chatRepository;
        this.chatClient = chatClient;
    }

    @Autowired
    private ChatServiceImpl myProxy;


    @Transactional(readOnly = true)
    @Override
    public List<ChatEntity> getAllChat() {
        return chatRepository.findAll(Sort.by(Sort.Direction.DESC, SORTED_FIELD));
    }

    @Override
    public ChatEntity getById(@NonNull Long chatId) {
        return chatRepository.findById(chatId)
                             .orElseThrow(() -> new IllegalArgumentException("Chat id should not be null"));
    }

    @Transactional
    @Override
    public ChatEntity createNew(String title) {
        ChatEntity chat = ChatEntity.builder().title(title).build();
        return chatRepository.save(chat);
    }

    @Override
    public void deleteChatById(Long chatId) {
        if (Objects.isNull(chatId)) {
            throw new IllegalArgumentException("Chat id must be set ");
        }
        chatRepository.removeChatById(chatId);
    }

    @Override
    public void proceedInteractionLLM(@NonNull Long chatId, @NonNull String promt) {
        myProxy.addChatEntry(chatId, promt, Role.USER);
        var answer = chatClient.prompt().user(promt).call().content();
        myProxy.addChatEntry(chatId, answer, Role.ASSISTANT);
    }

    @Override
    public SseEmitter proccesStreamingLLM(Long chatId, String userPromt) {
        myProxy.addChatEntry(chatId, userPromt, Role.USER);
        StringBuilder answer = new StringBuilder();
        final var emitter = new SseEmitter(0L);
        chatClient.prompt(userPromt)
                  .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                  .stream()
                  .chatResponse()
                  .subscribe(
                          response -> processToken(response, emitter, answer),
                          emitter::completeWithError,
                          emitter::complete
                  );


        return emitter;
    }

    @SneakyThrows
    private static void processToken(ChatResponse response, SseEmitter sseEmitter, StringBuilder answer) {
        var token = response.getResult().getOutput();
        sseEmitter.send(token);
        answer.append(token.getText());
    }

    @Transactional
    public void addChatEntry(@NonNull Long id, @NonNull String promt, Role role) {
        final var chat = chatRepository.findById(id).orElseThrow();
        chat.addChatEntry(ChatEntry.builder().content(promt).role(role).build());
    }
}
