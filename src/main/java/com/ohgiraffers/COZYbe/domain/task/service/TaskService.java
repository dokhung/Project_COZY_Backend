package com.ohgiraffers.COZYbe.domain.task.service;

import com.ohgiraffers.COZYbe.common.error.ApplicationException;
import com.ohgiraffers.COZYbe.common.error.ErrorCode;
import com.ohgiraffers.COZYbe.domain.member.domain.repository.MemberRepository;
import com.ohgiraffers.COZYbe.domain.projects.entity.Project;
import com.ohgiraffers.COZYbe.domain.projects.repository.ProjectRepository;
import com.ohgiraffers.COZYbe.domain.task.dto.*;
import com.ohgiraffers.COZYbe.domain.task.entity.Task;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskPriority;
import com.ohgiraffers.COZYbe.domain.task.enums.TaskStatus;
import com.ohgiraffers.COZYbe.domain.task.repository.TaskRepository;
import com.ohgiraffers.COZYbe.domain.task.repository.TaskCommentRepository;
import com.ohgiraffers.COZYbe.domain.task.repository.TaskHistoryRepository;
import com.ohgiraffers.COZYbe.domain.task.repository.TaskChecklistRepository;
import com.ohgiraffers.COZYbe.domain.task.repository.TaskAttachmentRepository;
import com.ohgiraffers.COZYbe.domain.task.repository.TaskNotificationRepository;
import com.ohgiraffers.COZYbe.domain.task.entity.TaskHistory;
import com.ohgiraffers.COZYbe.domain.teams.domain.entity.Team;
import com.ohgiraffers.COZYbe.domain.user.domain.entity.User;
import com.ohgiraffers.COZYbe.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final TaskCommentRepository commentRepository;
    private final TaskHistoryRepository historyRepository;
    private final TaskChecklistRepository checklistRepository;
    private final TaskAttachmentRepository attachmentRepository;
    private final TaskNotificationRepository notificationRepository;

    @Transactional
    public TaskCreateDTO createTask(TaskDTO dto, UUID currentUserId) {
        if (dto == null || dto.projectId() == null) {
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        }
        Project project = getProject(dto.projectId());
        assertTeamMember(project, currentUserId);
        validateTitle(dto.title());
        validateDates(dto.startDate(), dto.dueDate());

        UUID assigneeId = dto.assigneeId() == null ? currentUserId : dto.assigneeId();
        User assignee = getTeamAssignee(project, assigneeId);
        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_USER));
        Task savedTask = taskRepository.save(Task.builder()
                .project(project)
                .user(assignee)
                .creator(creator)
                .assignees(new LinkedHashSet<>(Set.of(assignee)))
                .title(dto.title().trim())
                .status(dto.status() == null ? TaskStatus.TODO : dto.status())
                .priority(dto.priority() == null ? TaskPriority.MEDIUM : dto.priority())
                .taskText(normalizeText(dto.taskText()))
                .startDate(dto.startDate())
                .dueDate(dto.dueDate())
                .build());
        record(savedTask, currentUserId, "CREATED", savedTask.getTitle());

        return toCreateDTO(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskListDTO> list(UUID projectId, UUID currentUserId) {
        Project project = getProject(projectId);
        assertTeamMember(project, currentUserId);
        return taskRepository.findAllByProject_ProjectId(projectId).stream()
                .map(this::toListDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskDetailDTO detail(UUID projectId, Long taskId, UUID currentUserId) {
        Task task = getTaskInProject(projectId, taskId);
        assertTeamMember(task.getProject(), currentUserId);
        return toDetailDTO(task);
    }

    @Transactional
    public TaskDetailDTO updateTask(
            UUID projectId,
            Long taskId,
            TaskUpdateDTO dto,
            UUID currentUserId
    ) {
        if (dto == null) {
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        }
        Task task = getTaskInProject(projectId, taskId);
        assertTeamMember(task.getProject(), currentUserId);

        if (dto.title() != null) {
            validateTitle(dto.title());
            task.setTitle(dto.title().trim());
        }
        if (dto.status() != null) {
            task.setStatus(dto.status());
        }
        if (dto.priority() != null) {
            task.setPriority(dto.priority());
        }
        if (dto.taskText() != null) {
            task.setTaskText(normalizeText(dto.taskText()));
        }
        if (dto.assigneeId() != null) {
            User assignee = getTeamAssignee(task.getProject(), dto.assigneeId());
            task.setUser(assignee);
            task.getAssignees().clear();
            task.getAssignees().add(assignee);
        }
        if (dto.startDate() != null) {
            task.setStartDate(dto.startDate());
        }
        if (dto.dueDate() != null) {
            task.setDueDate(dto.dueDate());
        }
        validateDates(task.getStartDate(), task.getDueDate());
        record(task, currentUserId, "UPDATED", task.getTitle());
        return toDetailDTO(task);
    }

    @Transactional
    public void deleteTask(UUID projectId, Long taskId, UUID currentUserId) {
        Task task = getTaskInProject(projectId, taskId);
        assertTeamMember(task.getProject(), currentUserId);
        if (!canDelete(task, currentUserId)) {
            throw new ApplicationException(ErrorCode.NOT_ALLOWED);
        }
        taskRepository.findByParentTask_TaskId(taskId).forEach(child -> child.setParentTask(null));
        commentRepository.deleteByTask_TaskId(taskId);
        checklistRepository.deleteByTask_TaskId(taskId);
        attachmentRepository.deleteByTask_TaskId(taskId);
        notificationRepository.deleteByTask_TaskId(taskId);
        historyRepository.deleteByTask_TaskId(taskId);
        taskRepository.delete(task);
    }

    private Project getProject(UUID projectId) {
        if (projectId == null) {
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        }
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_PROJECT));
    }

    private Task getTaskInProject(UUID projectId, Long taskId) {
        if (projectId == null || taskId == null) {
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        }
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_TASK));
        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new ApplicationException(ErrorCode.NO_SUCH_TASK);
        }
        return task;
    }

    private User getTeamAssignee(Project project, UUID assigneeId) {
        assertTeamMember(project, assigneeId);
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_SUCH_USER));
    }

    private void assertTeamMember(Project project, UUID userId) {
        if (userId == null || !memberRepository.existsByTeam_TeamIdAndUser_UserId(
                project.getTeam().getTeamId(), userId
        )) {
            throw new ApplicationException(ErrorCode.NOT_ALLOWED);
        }
    }

    private boolean canDelete(Task task, UUID userId) {
        User creator = task.getCreator() == null ? task.getUser() : task.getCreator();
        if (creator.getUserId().equals(userId)) {
            return true;
        }
        Team team = task.getProject().getTeam();
        return team.getLeader().getUserId().equals(userId)
                || (team.getSubLeader() != null && team.getSubLeader().getUserId().equals(userId));
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank() || title.trim().length() > 200) {
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        }
    }

    private void validateDates(LocalDate startDate, LocalDate dueDate) {
        if (startDate != null && dueDate != null && dueDate.isBefore(startDate)) {
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        }
    }

    private String normalizeText(String taskText) {
        if (taskText == null) {
            return null;
        }
        String normalized = taskText.trim();
        if (normalized.length() > 4000) {
            throw new ApplicationException(ErrorCode.INVALID_TASK);
        }
        return normalized;
    }

    private TaskCreateDTO toCreateDTO(Task task) {
        return new TaskCreateDTO(
                task.getTaskId(),
                task.getTitle(),
                task.getStatus(),
                task.getPriority(),
                task.getTaskText(),
                task.getUser().getUserId(),
                task.getUser().getNickname(),
                task.getStartDate(),
                task.getDueDate(),
                task.getCreatedAt()
        );
    }

    private TaskListDTO toListDTO(Task task) {
        return new TaskListDTO(
                task.getTaskId(),
                task.getTitle(),
                task.getUser().getNickname(),
                task.getStatus(),
                task.getPriority(),
                task.getTaskText(),
                task.getUser().getUserId(),
                task.getStartDate(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedDate()
        );
    }

    private TaskDetailDTO toDetailDTO(Task task) {
        return new TaskDetailDTO(
                task.getTaskId(),
                task.getProject().getProjectId(),
                task.getUser().getUserId(),
                task.getTitle(),
                task.getUser().getNickname(),
                task.getStatus(),
                task.getPriority(),
                task.getTaskText(),
                task.getStartDate(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedDate()
        );
    }

    private void record(Task task, UUID actorId, String action, String detail) {
        historyRepository.save(TaskHistory.builder()
                .task(task)
                .actor(userRepository.getReferenceById(actorId))
                .action(action)
                .detail(detail)
                .build());
    }
}
