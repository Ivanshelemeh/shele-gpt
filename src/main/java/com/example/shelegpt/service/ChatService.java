package com.example.shelegpt.service;

import com.example.shelegpt.entity.ChatEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ChatService {

    List<ChatEntity> getAllChat();

    ChatEntity getById(Long chatId);

    ChatEntity createNew(String title);

    void deleteChatById(Long chatId);

    void proceedInteractionLLM(Long chatId, String promt);

    SseEmitter proccesStreamingLLM(Long chatId, String userPromt);
}
