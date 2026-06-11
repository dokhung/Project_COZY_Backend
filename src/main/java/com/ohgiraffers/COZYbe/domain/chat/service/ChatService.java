package com.ohgiraffers.COZYbe.domain.chat.service;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import com.ohgiraffers.COZYbe.domain.chat.dto.*;
import com.ohgiraffers.COZYbe.domain.chat.entity.*;
import com.ohgiraffers.COZYbe.domain.chat.repository.*;
import com.ohgiraffers.COZYbe.domain.member.domain.repository.MemberRepository;
import com.ohgiraffers.COZYbe.domain.personal.entity.PersonalSchedule;
import com.ohgiraffers.COZYbe.domain.personal.repository.PersonalScheduleRepository;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.projects.repository.ProjectRepository;
import com.ohgiraffers.COZYbe.domain.teams.domain.entity.Team;
import com.ohgiraffers.COZYbe.domain.teams.domain.repository.TeamRepository;
import com.ohgiraffers.COZYbe.domain.user.domain.entity.User;
import com.ohgiraffers.COZYbe.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PersonalScheduleRepository personalScheduleRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Transactional(readOnly = true)
    public List<ChatRoomDTO> getRooms(ChatRoomType type, String contextName, String userId) {
        UUID currentUserId = UUID.fromString(userId);
        Context context = resolveContextAndCheckAccess(type, contextName, currentUserId);
        List<UUID> roomIds = chatRoomMemberRepository.findByUser_UserId(currentUserId)
                .stream()
                .map(member -> member.getChatRoom().getChatRoomId())
                .toList();
        if (roomIds.isEmpty()) return List.of();

        return chatRoomRepository
                .findByContextTypeAndContextIdAndChatRoomIdInOrderByUpdatedDateDesc(
                        type,
                        context.contextId(),
                        roomIds
                )
                .stream()
                .map(room -> toRoomDTO(room, currentUserId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatCandidateDTO> getCandidates(ChatRoomType type, String contextName, String userId) {
        Context context = resolveContextAndCheckAccess(type, contextName, UUID.fromString(userId));
        return memberRepository.findByTeam_TeamId(context.team().getTeamId())
                .orElse(List.of())
                .stream()
                .map(member -> new ChatCandidateDTO(
                        member.getUser().getUserId().toString(),
                        member.getUser().getNickname()
                ))
                .toList();
    }

    @Transactional
    public ChatRoomDTO createRoom(
            ChatRoomType type,
            String contextName,
            CreateChatRoomDTO dto,
            String userId
    ) {
        UUID creatorId = UUID.fromString(userId);
        Context context = resolveContextAndCheckAccess(type, contextName, creatorId);
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_USER));

        Set<UUID> memberIds = new LinkedHashSet<>();
        memberIds.add(creatorId);
        if (dto.memberIds() != null) {
            dto.memberIds().stream().map(UUID::fromString).forEach(memberIds::add);
        }
        if (dto.direct() && memberIds.size() != 2) {
            throw new ApplicationException(ErrorCode.INVALID_CHAT_ROOM);
        }
        for (UUID memberId : memberIds) {
            if (!memberRepository.existsByTeam_TeamIdAndUser_UserId(context.team().getTeamId(), memberId)) {
                throw new ApplicationException(ErrorCode.NOT_ALLOWED);
            }
        }

        String name = dto.name() == null ? "" : dto.name().trim();
        if (dto.direct()) {
            UUID otherId = memberIds.stream().filter(id -> !id.equals(creatorId)).findFirst().orElseThrow();
            name = userRepository.findById(otherId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_USER))
                    .getNickname();
        }
        if (name.isEmpty() || name.length() > 100) {
            throw new ApplicationException(ErrorCode.INVALID_CHAT_ROOM);
        }

        ChatRoom room = chatRoomRepository.save(ChatRoom.builder()
                .contextType(type)
                .contextId(context.contextId())
                .name(name)
                .direct(dto.direct())
                .createdBy(creator)
                .build());
        for (UUID memberId : memberIds) {
            chatRoomMemberRepository.save(ChatRoomMember.builder()
                    .chatRoom(room)
                    .user(userRepository.getReferenceById(memberId))
                    .build());
        }
        return toRoomDTO(room, creatorId);
    }

    @Transactional
    public void leaveRoom(String roomId, String userId) {
        UUID rid = UUID.fromString(roomId);
        UUID uid = UUID.fromString(userId);
        if (chatRoomMemberRepository.deleteMembership(rid, uid) == 0) {
            throw new ApplicationException(ErrorCode.NOT_ALLOWED);
        }
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getRoomMessages(String roomId, String userId) {
        UUID rid = requireRoomMember(roomId, userId);
        Map<UUID, PersonalSchedule> schedules = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        List<ChatMessageDTO> messages = new ArrayList<>(chatMessageRepository
                .findByChatRoom_ChatRoomIdOrderByCreatedAtDesc(rid, PageRequest.of(0, 100))
                .stream()
                .map(message -> toDTO(message, findCurrentSchedule(
                        message.getSender().getUserId(), now, schedules
                )))
                .toList());
        Collections.reverse(messages);
        return messages;
    }

    @Transactional
    public ChatMessageDTO sendToRoom(String roomId, String content, String userId) {
        String normalized = validateMessage(content);
        UUID rid = requireRoomMember(roomId, userId);
        UUID senderId = UUID.fromString(userId);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_USER));
        ChatRoom room = chatRoomRepository.findById(rid)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_CHAT_ROOM));
        ChatMessage saved = chatMessageRepository.save(ChatMessage.builder()
                .roomType(room.getContextType())
                .roomId(room.getContextId())
                .chatRoom(room)
                .sender(sender)
                .content(normalized)
                .build());
        return toDTO(saved, findCurrentSchedule(senderId, LocalDateTime.now(), new HashMap<>()));
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getMessages(ChatRoomType type, String roomName, String userId) {
        UUID roomId = resolveRoomAndCheckAccess(type, roomName, UUID.fromString(userId));
        Map<UUID, PersonalSchedule> currentSchedules = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        List<ChatMessageDTO> messages = new ArrayList<>(chatMessageRepository
                .findByRoomTypeAndRoomIdOrderByCreatedAtDesc(type, roomId, PageRequest.of(0, 100))
                .stream()
                .map(message -> toDTO(message, findCurrentSchedule(
                        message.getSender().getUserId(),
                        now,
                        currentSchedules
                )))
                .toList());
        Collections.reverse(messages);
        return messages;
    }

    @Transactional
    public ChatMessageDTO send(ChatRoomType type, String roomName, String content, String userId) {
        String normalized = validateMessage(content);

        UUID senderId = UUID.fromString(userId);
        UUID roomId = resolveRoomAndCheckAccess(type, roomName, senderId);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_USER));

        ChatMessage saved = chatMessageRepository.save(ChatMessage.builder()
                .roomType(type)
                .roomId(roomId)
                .sender(sender)
                .content(normalized)
                .build());
        return toDTO(saved, findCurrentSchedule(senderId, LocalDateTime.now(), new HashMap<>()));
    }

    private UUID resolveRoomAndCheckAccess(ChatRoomType type, String roomName, UUID userId) {
        return resolveContextAndCheckAccess(type, roomName, userId).contextId();
    }

    private Context resolveContextAndCheckAccess(ChatRoomType type, String roomName, UUID userId) {
        Team team;
        UUID roomId;

        if (type == ChatRoomType.TEAM) {
            team = teamRepository.findByTeamName(roomName)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_TEAM));
            roomId = team.getTeamId();
        } else {
            Project project = projectRepository.findByProjectName(roomName)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_PROJECT));
            team = project.getTeam();
            roomId = project.getProjectId();
        }

        if (!memberRepository.existsByTeam_TeamIdAndUser_UserId(team.getTeamId(), userId)) {
            throw new ApplicationException(ErrorCode.NOT_ALLOWED);
        }
        return new Context(roomId, team);
    }

    private UUID requireRoomMember(String roomId, String userId) {
        UUID rid = UUID.fromString(roomId);
        if (!chatRoomMemberRepository.existsByChatRoom_ChatRoomIdAndUser_UserId(
                rid,
                UUID.fromString(userId)
        )) {
            throw new ApplicationException(ErrorCode.NOT_ALLOWED);
        }
        return rid;
    }

    private String validateMessage(String content) {
        String normalized = content == null ? "" : content.trim();
        if (normalized.isEmpty() || normalized.length() > 1000) {
            throw new ApplicationException(ErrorCode.INVALID_CHAT_MESSAGE);
        }
        return normalized;
    }

    private ChatRoomDTO toRoomDTO(ChatRoom room, UUID currentUserId) {
        ChatMessage latest = chatMessageRepository
                .findFirstByChatRoom_ChatRoomIdOrderByCreatedAtDesc(room.getChatRoomId())
                .orElse(null);
        String displayName = room.getName();
        if (room.isDirect()) {
            displayName = chatRoomMemberRepository
                    .findWithUserByChatRoom_ChatRoomId(room.getChatRoomId())
                    .stream()
                    .map(ChatRoomMember::getUser)
                    .filter(user -> !user.getUserId().equals(currentUserId))
                    .map(User::getNickname)
                    .findFirst()
                    .orElse(room.getName());
        }
        return new ChatRoomDTO(
                room.getChatRoomId().toString(),
                displayName,
                room.isDirect(),
                chatRoomMemberRepository.countByChatRoom_ChatRoomId(room.getChatRoomId()),
                latest == null ? null : latest.getContent(),
                latest == null ? null : latest.getCreatedAt()
        );
    }

    private PersonalSchedule findCurrentSchedule(
            UUID userId,
            LocalDateTime now,
            Map<UUID, PersonalSchedule> cache
    ) {
        if (cache.containsKey(userId)) {
            return cache.get(userId);
        }
        PersonalSchedule schedule = personalScheduleRepository
                .findFirstByUser_UserIdAndStartAtLessThanEqualAndEndAtGreaterThanEqualOrderByStartAtDesc(
                        userId,
                        now,
                        now
                )
                .orElse(null);
        cache.put(userId, schedule);
        return schedule;
    }

    private ChatMessageDTO toDTO(ChatMessage message, PersonalSchedule currentSchedule) {
        User sender = message.getSender();
        return new ChatMessageDTO(
                message.getMessageId().toString(),
                sender.getUserId().toString(),
                sender.getNickname(),
                sender.getProfileImageUrl(),
                currentSchedule == null ? null : currentSchedule.getTitle(),
                currentSchedule == null ? null : currentSchedule.getStartAt(),
                currentSchedule == null ? null : currentSchedule.getEndAt(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    private record Context(UUID contextId, Team team) {
    }
}
