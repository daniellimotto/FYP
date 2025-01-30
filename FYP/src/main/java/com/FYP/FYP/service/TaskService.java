package com.FYP.FYP.service;

import com.FYP.FYP.model.Task;
import com.FYP.FYP.model.TaskStatus;
import com.FYP.FYP.model.Project;
import com.FYP.FYP.repository.TaskRepository;
import com.FYP.FYP.repository.ProjectRepository;
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

    public Task createTask(String title, String description, Long projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isPresent()) {
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setStatus(TaskStatus.TO_DO);
            task.setProject(projectOpt.get());
            return taskRepository.save(task);
        }
        return null;
    }

    public List<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(status);
            return taskRepository.save(task);
        }
        return null;
    }
}
