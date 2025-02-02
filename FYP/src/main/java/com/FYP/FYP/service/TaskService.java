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

    public Task createTask(String title, String description, Long projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isEmpty()) {
            return null;
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(TaskStatus.TO_DO);
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

    public Task updateTask(Long taskId, String description, TaskStatus status, Long assignedTo) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isEmpty()) {
            return null;
        }

        Task task = taskOpt.get();
        task.setDescription(description);
        task.setStatus(status);

        if (assignedTo != null) {
            Optional<User> userOpt = userRepository.findById(assignedTo);
            userOpt.ifPresent(task::setAssignedTo);
        }

        return taskRepository.save(task);
    }
}
