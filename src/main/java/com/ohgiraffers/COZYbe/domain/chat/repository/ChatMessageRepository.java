package com.ohgiraffers.COZYbe.domain.chat.repository;

import com.ohgiraffers.COZYbe.domain.chat.entity.ChatMessage;
import com.ohgiraffers.COZYbe.domain.chat.entity.ChatRoomType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    @EntityGraph(attributePaths = "sender")
    List<ChatMessage> findByRoomTypeAndRoomIdOrderByCreatedAtDesc(
            ChatRoomType roomType,
            UUID roomId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "sender")
    List<ChatMessage> findByChatRoom_ChatRoomIdOrderByCreatedAtDesc(UUID roomId, Pageable pageable);

    Optional<ChatMessage> findFirstByChatRoom_ChatRoomIdOrderByCreatedAtDesc(UUID roomId);
}
