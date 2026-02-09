package com.example.shelegpt.service.impl;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToolCompressor {

    private Encoding encoding;

    @PostConstruct
    public void init() {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        // cl100k_base используется Claude и GPT-4
        this.encoding = registry.getEncoding(EncodingType.CL100K_BASE);
        log.info("TokenCounter initialized with cl100k_base encoding");
    }

    /**
     * Подсчёт токенов в тексте
     */
    public int count(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return encoding.countTokens(text);
    }

    /**
     * Проверка не превышен ли лимит
     */
    public boolean exceedsLimit(String text, int limit) {
        return count(text) > limit;
    }

    /**
     * Обрезка текста до лимита токенов
     */
    public String truncateToLimit(String text, int maxTokens) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        List<Integer> tokens = encoding.encode(text).boxed();

        if (tokens.size() <= maxTokens) {
            return text;
        }

        List<Integer> truncated = tokens.subList(0, maxTokens);
        return encoding.decode((IntArrayList) truncated);
    }
}
