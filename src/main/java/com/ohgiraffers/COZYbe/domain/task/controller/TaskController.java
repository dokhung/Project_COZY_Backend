package com.ohgiraffers.COZYbe.domain.task.controller;

import com.ohgiraffers.COZYbe.domain.task.dto.*;
import com.ohgiraffers.COZYbe.domain.task.service.TaskService;
import com.ohgiraffers.COZYbe.domain.task.service.TaskCollaborationService;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskPriority;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskCollaborationService collaborationService;

    @PostMapping({"", "/create"})
    public ResponseEntity<TaskCreateDTO> createTask(
            @RequestBody TaskDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(taskService.createTask(dto, currentUserId(jwt)));
    }

    @GetMapping({"", "/list"})
    public ResponseEntity<List<TaskListDTO>> list(
            @RequestParam UUID projectId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(taskService.list(projectId, currentUserId(jwt)));
    }

    @GetMapping({"/{taskId}", "/detail"})
    public ResponseEntity<TaskDetailDTO> detail(
            @RequestParam UUID projectId,
            @PathVariable(required = false) Long taskId,
            @RequestParam(name = "taskId", required = false) Long legacyTaskId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long resolvedTaskId = taskId != null ? taskId : legacyTaskId;
        return ResponseEntity.ok(taskService.detail(projectId, resolvedTaskId, currentUserId(jwt)));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskDetailDTO> updateTask(
            @PathVariable Long taskId,
            @RequestParam UUID projectId,
            @RequestBody TaskUpdateDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                taskService.updateTask(projectId, taskId, dto, currentUserId(jwt))
        );
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @RequestParam UUID projectId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        taskService.deleteTask(projectId, taskId, currentUserId(jwt));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskListDTO>> search(
            @RequestParam UUID projectId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) UUID assigneeId,
            @RequestParam(required = false) LocalDate dueFrom,
            @RequestParam(required = false) LocalDate dueTo,
            @RequestParam(defaultValue = "false") boolean overdue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.search(projectId, currentUserId(jwt), keyword,
                status, priority, assigneeId, dueFrom, dueTo, overdue, page, size));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<TaskListDTO>> mine(
            @RequestParam(defaultValue = "false") boolean overdue,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.myTasks(currentUserId(jwt), overdue));
    }

    @PutMapping("/{taskId}/assignees")
    public ResponseEntity<List<TaskAssigneeDTO>> assignees(
            @PathVariable Long taskId, @RequestParam UUID projectId,
            @RequestBody Set<UUID> userIds, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.replaceAssignees(
                projectId, taskId, userIds, currentUserId(jwt)));
    }

    @PatchMapping("/{taskId}/board")
    public ResponseEntity<TaskListDTO> move(
            @PathVariable Long taskId, @RequestParam UUID projectId,
            @RequestBody TaskBoardMoveDTO dto, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.move(projectId, taskId, dto, currentUserId(jwt)));
    }

    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<TaskCommentDTO>> comments(
            @PathVariable Long taskId, @RequestParam UUID projectId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.comments(projectId, taskId, currentUserId(jwt)));
    }

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<TaskCommentDTO> addComment(
            @PathVariable Long taskId, @RequestParam UUID projectId,
            @RequestBody Map<String, Object> body, @AuthenticationPrincipal Jwt jwt) {
        Long parentId = body.get("parentCommentId") == null ? null
                : Long.valueOf(body.get("parentCommentId").toString());
        return ResponseEntity.ok(collaborationService.addComment(projectId, taskId, parentId,
                String.valueOf(body.getOrDefault("content", "")), currentUserId(jwt)));
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long taskId, @PathVariable Long commentId,
            @RequestParam UUID projectId, @AuthenticationPrincipal Jwt jwt) {
        collaborationService.deleteComment(projectId, taskId, commentId, currentUserId(jwt));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/history")
    public ResponseEntity<List<TaskHistoryDTO>> history(
            @PathVariable Long taskId, @RequestParam UUID projectId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.history(projectId, taskId, currentUserId(jwt)));
    }

    @GetMapping("/{taskId}/checklist")
    public ResponseEntity<List<TaskChecklistDTO>> checklist(
            @PathVariable Long taskId, @RequestParam UUID projectId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.checklist(projectId, taskId, currentUserId(jwt)));
    }

    @PostMapping("/{taskId}/checklist")
    public ResponseEntity<TaskChecklistDTO> addChecklist(
            @PathVariable Long taskId, @RequestParam UUID projectId,
            @RequestBody Map<String, String> body, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.addChecklist(projectId, taskId,
                body.get("content"), currentUserId(jwt)));
    }

    @PatchMapping("/{taskId}/checklist/{itemId}")
    public ResponseEntity<TaskChecklistDTO> updateChecklist(
            @PathVariable Long taskId, @PathVariable Long itemId, @RequestParam UUID projectId,
            @RequestBody Map<String, Object> body, @AuthenticationPrincipal Jwt jwt) {
        Boolean completed = body.get("completed") == null ? null
                : Boolean.valueOf(body.get("completed").toString());
        Long position = body.get("position") == null ? null : Long.valueOf(body.get("position").toString());
        return ResponseEntity.ok(collaborationService.updateChecklist(projectId, taskId, itemId,
                body.get("content") == null ? null : body.get("content").toString(),
                completed, position, currentUserId(jwt)));
    }

    @DeleteMapping("/{taskId}/checklist/{itemId}")
    public ResponseEntity<Void> deleteChecklist(
            @PathVariable Long taskId, @PathVariable Long itemId, @RequestParam UUID projectId,
            @AuthenticationPrincipal Jwt jwt) {
        collaborationService.deleteChecklist(projectId, taskId, itemId, currentUserId(jwt));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/attachments")
    public ResponseEntity<List<TaskAttachmentDTO>> attachments(
            @PathVariable Long taskId, @RequestParam UUID projectId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.attachments(projectId, taskId, currentUserId(jwt)));
    }

    @PostMapping(value = "/{taskId}/attachments", consumes = "multipart/form-data")
    public ResponseEntity<TaskAttachmentDTO> attach(
            @PathVariable Long taskId, @RequestParam UUID projectId,
            @RequestPart("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.attach(projectId, taskId, file, currentUserId(jwt)));
    }

    @DeleteMapping("/{taskId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long taskId, @PathVariable Long attachmentId, @RequestParam UUID projectId,
            @AuthenticationPrincipal Jwt jwt) {
        collaborationService.deleteAttachment(projectId, taskId, attachmentId, currentUserId(jwt));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/parent")
    public ResponseEntity<TaskListDTO> parent(
            @PathVariable Long taskId, @RequestParam UUID projectId,
            @RequestBody Map<String, Long> body, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.setParent(projectId, taskId,
                body.get("parentTaskId"), currentUserId(jwt)));
    }

    @PatchMapping("/{taskId}/recurrence")
    public ResponseEntity<TaskListDTO> recurrence(
            @PathVariable Long taskId, @RequestParam UUID projectId,
            @RequestBody TaskRecurrenceDTO dto, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.recurrence(
                projectId, taskId, dto, currentUserId(jwt)));
    }

    @PostMapping("/{taskId}/recurrence/next")
    public ResponseEntity<TaskListDTO> nextOccurrence(
            @PathVariable Long taskId, @RequestParam UUID projectId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.createNextOccurrence(
                projectId, taskId, currentUserId(jwt)));
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<TaskNotificationDTO>> notifications(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(collaborationService.notifications(currentUserId(jwt), unreadOnly));
    }

    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> readNotification(
            @PathVariable Long notificationId, @AuthenticationPrincipal Jwt jwt) {
        collaborationService.readNotification(notificationId, currentUserId(jwt));
        return ResponseEntity.noContent().build();
    }

    private UUID currentUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
