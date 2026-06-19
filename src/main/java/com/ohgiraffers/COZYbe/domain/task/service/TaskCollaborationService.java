package com.ohgiraffers.COZYbe.domain.task.service;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import com.ohgiraffers.COZYbe.domain.files.service.LocalFileStorageService;
import com.ohgiraffers.COZYbe.domain.member.domain.repository.MemberRepository;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.projects.repository.ProjectRepository;
import com.ohgiraffers.COZYbe.domain.task.dto.*;
import com.ohgiraffers.COZYbe.domain.task.entity.*;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskPriority;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskStatus;
import com.ohgiraffers.COZYbe.domain.task.repository.*;
import com.ohgiraffers.COZYbe.domain.user.domain.entity.User;
import com.ohgiraffers.COZYbe.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskCollaborationService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskCommentRepository commentRepository;
    private final TaskHistoryRepository historyRepository;
    private final TaskChecklistRepository checklistRepository;
    private final TaskAttachmentRepository attachmentRepository;
    private final TaskNotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final LocalFileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<TaskListDTO> search(UUID projectId, UUID currentUserId, String keyword,
                                    TaskStatus status, TaskPriority priority, UUID assigneeId,
                                    LocalDate dueFrom, LocalDate dueTo, boolean overdue,
                                    int page, int size) {
        requireProjectAccess(projectId, currentUserId);
        List<Task> filtered = taskRepository.findAllByProject_ProjectIdOrderByStatusAscBoardPositionAsc(projectId)
                .stream()
                .filter(t -> keyword == null || keyword.isBlank()
                        || t.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || (t.getTaskText() != null && t.getTaskText().toLowerCase().contains(keyword.toLowerCase())))
                .filter(t -> status == null || t.getStatus() == status)
                .filter(t -> priority == null || t.getPriority() == priority)
                .filter(t -> assigneeId == null || t.getAssignees().stream()
                        .anyMatch(u -> u.getUserId().equals(assigneeId)))
                .filter(t -> dueFrom == null || (t.getDueDate() != null && !t.getDueDate().isBefore(dueFrom)))
                .filter(t -> dueTo == null || (t.getDueDate() != null && !t.getDueDate().isAfter(dueTo)))
                .filter(t -> !overdue || (t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now())
                        && t.getStatus() != TaskStatus.DONE))
                .toList();
        int safeSize = Math.min(Math.max(size, 1), 100);
        int from = Math.min(Math.max(page, 0) * safeSize, filtered.size());
        int to = Math.min(from + safeSize, filtered.size());
        return filtered.subList(from, to).stream().map(this::toList).toList();
    }

    @Transactional(readOnly = true)
    public List<TaskListDTO> myTasks(UUID currentUserId, boolean overdue) {
        return taskRepository.findDistinctByAssignees_UserIdOrderByDueDateAsc(currentUserId).stream()
                .filter(t -> !overdue || (t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now())
                        && t.getStatus() != TaskStatus.DONE))
                .map(this::toList).toList();
    }

    @Transactional
    public List<TaskAssigneeDTO> replaceAssignees(UUID projectId, Long taskId,
                                                   Set<UUID> ids, UUID actorId) {
        Task task = requireTask(projectId, taskId, actorId);
        if (ids == null || ids.isEmpty()) throw new ApplicationException(ErrorCode.INVALID_TASK);
        LinkedHashSet<User> users = new LinkedHashSet<>();
        for (UUID id : ids) {
            assertMember(task, id);
            users.add(userRepository.findById(id)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_USER)));
        }
        task.getAssignees().clear();
        task.getAssignees().addAll(users);
        task.setUser(users.iterator().next());
        record(task, actorId, "ASSIGNEES_CHANGED", ids.toString());
        users.stream().filter(u -> !u.getUserId().equals(actorId))
                .forEach(u -> notify(task, u, "ASSIGNED", "업무 담당자로 지정되었습니다."));
        return users.stream().map(this::toAssignee).toList();
    }

    @Transactional
    public TaskListDTO move(UUID projectId, Long taskId, TaskBoardMoveDTO dto, UUID actorId) {
        Task task = requireTask(projectId, taskId, actorId);
        if (dto == null || dto.status() == null || dto.position() == null || dto.position() < 0)
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        task.setStatus(dto.status());
        task.setBoardPosition(dto.position());
        record(task, actorId, "BOARD_MOVED", dto.status() + ":" + dto.position());
        return toList(task);
    }

    @Transactional
    public TaskCommentDTO addComment(UUID projectId, Long taskId, Long parentId,
                                     String content, UUID actorId) {
        Task task = requireTask(projectId, taskId, actorId);
        String normalized = content == null ? "" : content.trim();
        if (normalized.isEmpty() || normalized.length() > 2000)
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        TaskComment parent = parentId == null ? null : commentRepository.findById(parentId)
                .filter(c -> c.getTask().getTaskId().equals(taskId))
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_TASK));
        User actor = getUser(actorId);
        TaskComment saved = commentRepository.save(TaskComment.builder()
                .task(task).author(actor).parentComment(parent).content(normalized).build());
        record(task, actorId, "COMMENT_ADDED", normalized);
        task.getAssignees().stream().filter(u -> !u.getUserId().equals(actorId))
                .forEach(u -> notify(task, u, "COMMENT", "담당 업무에 새 댓글이 등록되었습니다."));
        return toComment(saved);
    }

    @Transactional(readOnly = true)
    public List<TaskCommentDTO> comments(UUID projectId, Long taskId, UUID actorId) {
        requireTask(projectId, taskId, actorId);
        return commentRepository.findByTask_TaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(this::toComment).toList();
    }

    @Transactional
    public void deleteComment(UUID projectId, Long taskId, Long commentId, UUID actorId) {
        Task task = requireTask(projectId, taskId, actorId);
        TaskComment comment = commentRepository.findById(commentId)
                .filter(c -> c.getTask().getTaskId().equals(taskId))
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_TASK));
        if (!comment.getAuthor().getUserId().equals(actorId) && !isLeader(task, actorId))
            throw new ApplicationException(ErrorCode.NOT_ALLOWED);
        commentRepository.delete(comment);
        record(task, actorId, "COMMENT_DELETED", String.valueOf(commentId));
    }

    @Transactional(readOnly = true)
    public List<TaskHistoryDTO> history(UUID projectId, Long taskId, UUID actorId) {
        requireTask(projectId, taskId, actorId);
        return historyRepository.findByTask_TaskIdOrderByCreatedAtDesc(taskId).stream()
                .map(h -> new TaskHistoryDTO(h.getHistoryId(), h.getActor().getUserId(),
                        h.getActor().getNickname(), h.getAction(), h.getDetail(), h.getCreatedAt()))
                .toList();
    }

    @Transactional
    public TaskChecklistDTO addChecklist(UUID projectId, Long taskId, String content, UUID actorId) {
        Task task = requireTask(projectId, taskId, actorId);
        String value = content == null ? "" : content.trim();
        if (value.isEmpty() || value.length() > 500) throw new ApplicationException(ErrorCode.INVALID_TASK);
        long position = checklistRepository.findByTask_TaskIdOrderByPositionAsc(taskId).size();
        TaskChecklist item = checklistRepository.save(TaskChecklist.builder()
                .task(task).content(value).completed(false).position(position).build());
        record(task, actorId, "CHECKLIST_ADDED", value);
        return toChecklist(item);
    }

    @Transactional
    public TaskChecklistDTO updateChecklist(UUID projectId, Long taskId, Long itemId,
                                            String content, Boolean completed, Long position,
                                            UUID actorId) {
        Task task = requireTask(projectId, taskId, actorId);
        TaskChecklist item = checklistRepository.findById(itemId)
                .filter(i -> i.getTask().getTaskId().equals(taskId))
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_TASK));
        if (content != null) {
            if (content.isBlank() || content.trim().length() > 500)
                throw new ApplicationException(ErrorCode.INVALID_TASK);
            item.setContent(content.trim());
        }
        if (completed != null) item.setCompleted(completed);
        if (position != null && position >= 0) item.setPosition(position);
        record(task, actorId, "CHECKLIST_CHANGED", String.valueOf(itemId));
        return toChecklist(item);
    }

    @Transactional(readOnly = true)
    public List<TaskChecklistDTO> checklist(UUID projectId, Long taskId, UUID actorId) {
        requireTask(projectId, taskId, actorId);
        return checklistRepository.findByTask_TaskIdOrderByPositionAsc(taskId).stream()
                .map(this::toChecklist).toList();
    }

    @Transactional
    public void deleteChecklist(UUID projectId, Long taskId, Long itemId, UUID actorId) {
        Task task = requireTask(projectId, taskId, actorId);
        TaskChecklist item = checklistRepository.findById(itemId)
                .filter(i -> i.getTask().getTaskId().equals(taskId))
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_TASK));
        checklistRepository.delete(item);
        record(task, actorId, "CHECKLIST_DELETED", String.valueOf(itemId));
    }

    @Transactional
    public TaskAttachmentDTO attach(UUID projectId, Long taskId, MultipartFile file, UUID actorId) {
        Task task = requireTask(projectId, taskId, actorId);
        try {
            String key = fileStorageService.uploadTaskAttachment(file);
            TaskAttachment saved = attachmentRepository.save(TaskAttachment.builder()
                    .task(task).uploader(getUser(actorId)).originalName(
                            Optional.ofNullable(file.getOriginalFilename()).orElse("file"))
                    .storageKey(key).contentType(file.getContentType()).size(file.getSize()).build());
            record(task, actorId, "ATTACHMENT_ADDED", saved.getOriginalName());
            return toAttachment(saved);
        } catch (IOException | IllegalArgumentException e) {
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        }
    }

    @Transactional(readOnly = true)
    public List<TaskAttachmentDTO> attachments(UUID projectId, Long taskId, UUID actorId) {
        requireTask(projectId, taskId, actorId);
        return attachmentRepository.findByTask_TaskIdOrderByCreatedAtDesc(taskId).stream()
                .map(this::toAttachment).toList();
    }

    @Transactional
    public void deleteAttachment(UUID projectId, Long taskId, Long attachmentId, UUID actorId) {
        Task task = requireTask(projectId, taskId, actorId);
        TaskAttachment attachment = attachmentRepository.findById(attachmentId)
                .filter(a -> a.getTask().getTaskId().equals(taskId))
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_TASK));
        if (!attachment.getUploader().getUserId().equals(actorId) && !isLeader(task, actorId))
            throw new ApplicationException(ErrorCode.NOT_ALLOWED);
        attachmentRepository.delete(attachment);
        record(task, actorId, "ATTACHMENT_DELETED", attachment.getOriginalName());
    }

    @Transactional
    public TaskListDTO setParent(UUID projectId, Long taskId, Long parentId, UUID actorId) {
        Task task = requireTask(projectId, taskId, actorId);
        if (Objects.equals(taskId, parentId)) throw new ApplicationException(ErrorCode.INVALID_TASK);
        Task parent = parentId == null ? null : requireTask(projectId, parentId, actorId);
        task.setParentTask(parent);
        record(task, actorId, "PARENT_CHANGED", String.valueOf(parentId));
        return toList(task);
    }

    @Transactional
    public TaskListDTO recurrence(UUID projectId, Long taskId, TaskRecurrenceDTO dto, UUID actorId) {
        Task task = requireTask(projectId, taskId, actorId);
        String type = dto == null || dto.type() == null ? "NONE" : dto.type().toUpperCase();
        if (!Set.of("NONE", "DAILY", "WEEKLY", "MONTHLY").contains(type)
                || (dto.interval() != null && dto.interval() < 1))
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        task.setRecurrenceType(type);
        task.setRecurrenceInterval(dto.interval() == null ? 1 : dto.interval());
        task.setRecurrenceUntil(dto.until());
        record(task, actorId, "RECURRENCE_CHANGED", type);
        return toList(task);
    }

    @Transactional
    public TaskListDTO createNextOccurrence(UUID projectId, Long taskId, UUID actorId) {
        Task source = requireTask(projectId, taskId, actorId);
        String type = source.getRecurrenceType();
        int interval = source.getRecurrenceInterval() == null ? 1 : source.getRecurrenceInterval();
        if (type == null || type.equals("NONE") || source.getDueDate() == null)
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        LocalDate nextDue = switch (type) {
            case "DAILY" -> source.getDueDate().plusDays(interval);
            case "WEEKLY" -> source.getDueDate().plusWeeks(interval);
            case "MONTHLY" -> source.getDueDate().plusMonths(interval);
            default -> throw new ApplicationException(ErrorCode.INVALID_TASK);
        };
        if (source.getRecurrenceUntil() != null && nextDue.isAfter(source.getRecurrenceUntil()))
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        long duration = source.getStartDate() == null ? 0
                : java.time.temporal.ChronoUnit.DAYS.between(source.getStartDate(), source.getDueDate());
        Task next = taskRepository.save(Task.builder()
                .project(source.getProject()).creator(getUser(actorId)).user(source.getUser())
                .assignees(new LinkedHashSet<>(source.getAssignees())).title(source.getTitle())
                .taskText(source.getTaskText()).status(TaskStatus.TODO).priority(source.getPriority())
                .startDate(source.getStartDate() == null ? null : nextDue.minusDays(duration))
                .dueDate(nextDue).recurrenceType(type).recurrenceInterval(interval)
                .recurrenceUntil(source.getRecurrenceUntil()).parentTask(source.getParentTask()).build());
        record(next, actorId, "RECURRENCE_CREATED", "source=" + source.getTaskId());
        return toList(next);
    }

    @Transactional(readOnly = true)
    public List<TaskNotificationDTO> notifications(UUID userId, boolean unreadOnly) {
        return notificationRepository.findByRecipient_UserIdOrderByCreatedAtDesc(userId).stream()
                .filter(n -> !unreadOnly || !n.isRead())
                .map(n -> new TaskNotificationDTO(n.getNotificationId(), n.getTask().getTaskId(),
                        n.getType(), n.getMessage(), n.isRead(), n.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void readNotification(Long notificationId, UUID userId) {
        TaskNotification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getRecipient().getUserId().equals(userId))
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_ALLOWED));
        notification.setRead(true);
    }

    private Project requireProjectAccess(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_PROJECT));
        if (!memberRepository.existsByTeam_TeamIdAndUser_UserId(project.getTeam().getTeamId(), userId))
            throw new ApplicationException(ErrorCode.NOT_ALLOWED);
        return project;
    }

    private Task requireTask(UUID projectId, Long taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getProject().getProjectId().equals(projectId))
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_TASK));
        assertMember(task, userId);
        return task;
    }

    private void assertMember(Task task, UUID userId) {
        if (!memberRepository.existsByTeam_TeamIdAndUser_UserId(
                task.getProject().getTeam().getTeamId(), userId))
            throw new ApplicationException(ErrorCode.NOT_ALLOWED);
    }

    private boolean isLeader(Task task, UUID id) {
        return task.getProject().getTeam().getLeader().getUserId().equals(id)
                || (task.getProject().getTeam().getSubLeader() != null
                && task.getProject().getTeam().getSubLeader().getUserId().equals(id));
    }

    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_USER));
    }

    private void record(Task task, UUID actorId, String action, String detail) {
        historyRepository.save(TaskHistory.builder().task(task).actor(getUser(actorId))
                .action(action).detail(detail).build());
    }

    private void notify(Task task, User recipient, String type, String message) {
        notificationRepository.save(TaskNotification.builder().task(task).recipient(recipient)
                .type(type).message(message).read(false).build());
    }

    private TaskListDTO toList(Task t) {
        return new TaskListDTO(t.getTaskId(), t.getTitle(), t.getUser().getNickname(), t.getStatus(),
                t.getPriority(), t.getTaskText(), t.getUser().getUserId(), t.getStartDate(),
                t.getDueDate(), t.getCreatedAt(), t.getUpdatedDate());
    }
    private TaskCommentDTO toComment(TaskComment c) {
        return new TaskCommentDTO(c.getCommentId(),
                c.getParentComment() == null ? null : c.getParentComment().getCommentId(),
                c.getAuthor().getUserId(), c.getAuthor().getNickname(), c.getContent(), c.getCreatedAt());
    }
    private TaskChecklistDTO toChecklist(TaskChecklist i) {
        return new TaskChecklistDTO(i.getChecklistId(), i.getContent(), i.isCompleted(), i.getPosition());
    }
    private TaskAttachmentDTO toAttachment(TaskAttachment a) {
        return new TaskAttachmentDTO(a.getAttachmentId(), a.getOriginalName(),
                fileStorageService.getPublicUrl(a.getStorageKey()), a.getContentType(), a.getSize(), a.getCreatedAt());
    }
    private TaskAssigneeDTO toAssignee(User u) {
        return new TaskAssigneeDTO(u.getUserId(), u.getNickname(), u.getProfileImageUrl());
    }
}
