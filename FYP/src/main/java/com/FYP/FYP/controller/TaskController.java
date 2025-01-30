package com.FYP.FYP.controller;

import com.FYP.FYP.model.Task;
import com.FYP.FYP.model.TaskStatus;
import com.FYP.FYP.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/{projectId}")
    public String listTasks(@PathVariable Long projectId, Model model) {
        List<Task> tasks = taskService.getTasksByProject(projectId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("projectId", projectId);
        return "tasks/list";
    }

    @GetMapping("/create/{projectId}")
    public String showCreateTaskForm(@PathVariable Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        return "tasks/create";
    }

    @PostMapping("/create/{projectId}")
    public String createTask(@PathVariable Long projectId,
                             @RequestParam String title,
                             @RequestParam String description) {
        taskService.createTask(title, description, projectId);
        return "redirect:/tasks/" + projectId;
    }

    @PostMapping("/update/{taskId}")
    public String updateTaskStatus(@PathVariable Long taskId, @RequestParam TaskStatus status) {
        taskService.updateTaskStatus(taskId, status);
        return "redirect:/tasks/" + taskService.getTasksByProject(taskId).get(0).getProject().getId();
    }
}
