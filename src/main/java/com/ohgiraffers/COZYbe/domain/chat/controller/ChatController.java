package com.ohgiraffers.COZYbe.domain.chat.controller;

import com.ohgiraffers.COZYbe.domain.chat.dto.*;
import com.ohgiraffers.COZYbe.domain.chat.entity.ChatRoomType;
import com.ohgiraffers.COZYbe.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{type}/{roomName}/rooms")
    public ResponseEntity<List<ChatRoomDTO>> getRooms(
            @PathVariable ChatRoomType type,
            @PathVariable String roomName,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(chatService.getRooms(type, roomName, jwt.getSubject()));
    }

    @GetMapping("/{type}/{roomName}/candidates")
    public ResponseEntity<List<ChatCandidateDTO>> getCandidates(
            @PathVariable ChatRoomType type,
            @PathVariable String roomName,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(chatService.getCandidates(type, roomName, jwt.getSubject()));
    }

    @PostMapping("/{type}/{roomName}/rooms")
    public ResponseEntity<ChatRoomDTO> createRoom(
            @PathVariable ChatRoomType type,
            @PathVariable String roomName,
            @RequestBody CreateChatRoomDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(chatService.createRoom(type, roomName, dto, jwt.getSubject()));
    }

    @DeleteMapping("/rooms/{roomId}/members/me")
    public ResponseEntity<Void> leaveRoom(
            @PathVariable String roomId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        chatService.leaveRoom(roomId, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getRoomMessages(
            @PathVariable String roomId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(chatService.getRoomMessages(roomId, jwt.getSubject()));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessageDTO> sendToRoom(
            @PathVariable String roomId,
            @RequestBody CreateChatMessageDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(chatService.sendToRoom(roomId, dto.content(), jwt.getSubject()));
    }

    @GetMapping("/{type}/{roomName}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
            @PathVariable ChatRoomType type,
            @PathVariable String roomName,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(chatService.getMessages(type, roomName, jwt.getSubject()));
    }

    @PostMapping("/{type}/{roomName}/messages")
    public ResponseEntity<ChatMessageDTO> send(
            @PathVariable ChatRoomType type,
            @PathVariable String roomName,
            @RequestBody CreateChatMessageDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(chatService.send(type, roomName, dto.content(), jwt.getSubject()));
    }
}
