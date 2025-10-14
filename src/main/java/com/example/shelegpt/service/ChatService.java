package com.example.shelegpt.service;

import com.example.shelegpt.entity.ChatEntity;
import com.example.shelegpt.entity.Role;

import java.util.List;

public interface ChatService {

    List<ChatEntity> getAllChat();

    ChatEntity getById(Long chatId);

    ChatEntity createNew(String title);

    void deleteChatById(Long chatId);

    void proceedInteractionLLM(Long chatId, String promt);
}
