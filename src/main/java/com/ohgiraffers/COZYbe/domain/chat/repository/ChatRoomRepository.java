package com.ohgiraffers.COZYbe.domain.chat.repository;

import com.ohgiraffers.COZYbe.domain.chat.entity.ChatRoom;
import com.ohgiraffers.COZYbe.domain.chat.entity.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    List<ChatRoom> findByContextTypeAndContextIdAndChatRoomIdInOrderByUpdatedDateDesc(
            ChatRoomType contextType,
            UUID contextId,
            List<UUID> chatRoomIds
    );
}
