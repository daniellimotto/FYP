package com.FYP.FYP.service;

import com.FYP.FYP.model.Task;
import com.FYP.FYP.model.TaskStatus;
import com.FYP.FYP.model.Project;
import com.FYP.FYP.model.User;
import com.FYP.FYP.repository.TaskRepository;
import com.FYP.FYP.repository.ProjectRepository;
import com.FYP.FYP.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public Task createTask(String title, String description, Date dueDate, Long projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isEmpty()) {
            return null;
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(TaskStatus.TO_DO);
        task.setDueDate(dueDate);
        task.setProject(projectOpt.get());

        return taskRepository.save(task);
    }

    public List<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isEmpty()) {
            return null;
        }

        Task task = taskOpt.get();
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, String description, TaskStatus status, Long assignedTo, Date dueDate) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isEmpty()) {
            return null;
        }

        Task task = taskOpt.get();
        task.setDescription(description);
        task.setStatus(status);
        task.setDueDate(dueDate);

        if (assignedTo != null) {
            Optional<User> userOpt = userRepository.findById(assignedTo);
            userOpt.ifPresent(user -> {
                task.setAssignedTo(user);
                notificationService.createNotification(user, "You have been assigned to a task: " + task.getTitle());
            });
        }

        if (task.getAssignedTo() != null) {
            notificationService.createNotification(task.getAssignedTo(), "Task updated: " + task.getTitle());
        }

        return taskRepository.save(task);
    }

    public boolean deleteTask(Long taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isPresent()) {
            taskRepository.deleteById(taskId);
            return true;
        }

        return false;
    }
}
