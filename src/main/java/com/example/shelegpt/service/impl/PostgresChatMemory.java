package com.example.shelegpt.service.impl;

import com.example.shelegpt.entity.ChatEntity;
import com.example.shelegpt.entity.ChatEntry;
import com.example.shelegpt.repo.ChatRepository;
import lombok.Builder;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static com.example.shelegpt.entity.ChatEntry.toChatEntry;

@Builder
public class PostgresChatMemory implements ChatMemory {


    private  ChatRepository chatRepository;
    private int maxMessages;


    @Override
    @Transactional
    public void add(@NonNull String conversationId, List<Message> messages) {
        ChatEntity chat = chatRepository.findById(Long.valueOf(conversationId)).
                                        orElseThrow(()-> new IllegalArgumentException("not found any chat within" + conversationId));
        for (Message message : messages) {
            chat.addChatEntry(toChatEntry(message));
        }
        chatRepository.save(chat);
    }

    @Override
    public List<Message> get(@NonNull String conversationId) {
        ChatEntity chat = chatRepository.findById(Long.valueOf(conversationId)).orElseThrow();
        return chat.getHistory()
                .stream()
                   .sorted(Comparator.comparing(ChatEntry::getCreatedAt).reversed())
                   .map(ChatEntry::toMessage)
                   .limit(maxMessages)
                   .toList();

    }

    @Override
    public void clear(@NonNull String conversationId) {
      // not impl
    }
}
