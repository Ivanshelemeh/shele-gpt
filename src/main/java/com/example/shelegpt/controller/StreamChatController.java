package com.example.shelegpt.controller;

import com.example.shelegpt.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class StreamChatController {

    private final ChatService chatService;

    @GetMapping(value = "/chat-stream/{chatId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTalkToLLM(@PathVariable Long chatId, @RequestParam String userPromt) {
       SseEmitter emitter = chatService.proccesStreamingLLM(chatId,userPromt);

    }
}
