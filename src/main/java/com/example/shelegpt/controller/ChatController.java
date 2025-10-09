package com.example.shelegpt.controller;

import com.example.shelegpt.entity.ChatEntity;
import com.example.shelegpt.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private static final String MAIN_PAGE = "/";
    private static final String CHAT_PAGE = "chat";
    private static final String REDIRECT_PAGE = "redirect:/chat/";
    private final ChatService chatService;

    @GetMapping("/")
    public String mainPage(ModelMap model) {
        model.addAttribute("chats", chatService.getAllChat());
        return CHAT_PAGE;
    }

    @GetMapping("/chat/{chatId}")
    public String showChat(@PathVariable Long chatId, ModelMap modelMap) {
        modelMap.addAttribute("chats", chatService.getAllChat());
        modelMap.addAttribute("chat", chatService.getById(chatId));
        return CHAT_PAGE;
    }

    @PostMapping("/chat/new")
    public String newChat(@RequestParam String title) {
        ChatEntity entity = chatService.createNew(title);
        return REDIRECT_PAGE + entity.getId();

    }

    @PostMapping("/chat/{chatId}/delete")
    public String deleteChat(@PathVariable Long chatId) {
        return MAIN_PAGE;
    }

    @PostMapping("/chat/{chatId}/entry")
    public String talkToLLM(
            @PathVariable Long chatId,
            @RequestParam String promt
    ) {
        chatService.proceedInteractionLLM(chatId, promt);
        return REDIRECT_PAGE + chatId;

    }

}
