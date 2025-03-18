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

    public Task createTask(String title, String description, Date dueDate, int projectId) {
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

    public List<Task> getTasksByProject(int projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Optional<Task> getTaskById(int taskId) {
        return taskRepository.findById(taskId);
    }

    public Task updateTaskStatus(int taskId, TaskStatus status) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isEmpty()) {
            return null;
        }

        Task task = taskOpt.get();
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public Task updateTask(int taskId, String description, TaskStatus status, Integer assignedTo, Date dueDate) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isEmpty()) {
            return null;
        }

        Task task = taskOpt.get();
        
        String oldStatus = task.getStatus().name();
        Date oldDueDate = task.getDueDate();
        User oldAssignedUser = task.getAssignedTo();

        boolean notify = false;

        task.setDescription(description);
        
        if (!task.getStatus().equals(status)) {
            task.setStatus(status);
            notificationService.createNotification(task.getAssignedTo(), "Task status updated: " + task.getTitle());
            notify = true;
        }

        if (!oldDueDate.equals(dueDate)) {
            task.setDueDate(dueDate);
            notificationService.createNotification(task.getAssignedTo(), "Task deadline changed: " + task.getTitle());
            notify = true;
        }

        if (assignedTo != null) {
            Optional<User> userOpt = userRepository.findById(assignedTo);
            if (userOpt.isPresent()) {
                User newAssignedUser = userOpt.get();
                task.setAssignedTo(newAssignedUser);

                if (oldAssignedUser == null) {
                    notificationService.createNotification(newAssignedUser, "You have been assigned to a task: " + task.getTitle());
                    notify = true;
                } else if (!oldAssignedUser.equals(newAssignedUser)) {
                    notificationService.createNotification(newAssignedUser, "You have been reassigned to task: " + task.getTitle());
                    notificationService.createNotification(oldAssignedUser, "Task reassigned to another user: " + task.getTitle());
                    notify = true;
                }
            }
        }

        if (notify) {
            return taskRepository.save(task);
        }

        return task;
    }

    public boolean deleteTask(int taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isPresent()) {
            taskRepository.deleteById(taskId);
            return true;
        }

        return false;
    }
}
