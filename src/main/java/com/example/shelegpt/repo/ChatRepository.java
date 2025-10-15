package com.example.shelegpt.repo;

import com.example.shelegpt.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    @Query("DELETE FROM ChatEntity c WHERE c.id =: chatId  ")
    @Modifying
    void removeChatById(@Param("chatId") Long chatId);
}
