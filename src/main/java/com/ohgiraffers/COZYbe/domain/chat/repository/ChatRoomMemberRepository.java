package com.ohgiraffers.COZYbe.domain.chat.repository;

import com.ohgiraffers.COZYbe.domain.chat.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, UUID> {
    List<ChatRoomMember> findByUser_UserId(UUID userId);
    List<ChatRoomMember> findByChatRoom_ChatRoomId(UUID roomId);
    boolean existsByChatRoom_ChatRoomIdAndUser_UserId(UUID roomId, UUID userId);
    Optional<ChatRoomMember> findByChatRoom_ChatRoomIdAndUser_UserId(UUID roomId, UUID userId);
    long countByChatRoom_ChatRoomId(UUID roomId);

    @EntityGraph(attributePaths = "user")
    List<ChatRoomMember> findWithUserByChatRoom_ChatRoomId(UUID roomId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            delete from ChatRoomMember member
            where member.chatRoom.chatRoomId = :roomId
              and member.user.userId = :userId
            """)
    int deleteMembership(@Param("roomId") UUID roomId, @Param("userId") UUID userId);
}
