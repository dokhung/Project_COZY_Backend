package com.ohgiraffers.COZYbe.domain.chat.entity;

import com.ohgiraffers.COZYbe.common.BaseTimeEntity;
import com.ohgiraffers.COZYbe.domain.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tbl_chat_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID chatRoomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatRoomType contextType;

    @Column(nullable = false)
    private UUID contextId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean direct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User createdBy;
}
