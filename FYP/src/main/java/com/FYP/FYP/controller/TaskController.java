package com.FYP.FYP.controller;

import com.FYP.FYP.model.Task;
import com.FYP.FYP.model.TaskStatus;
import com.FYP.FYP.model.User;
import com.FYP.FYP.service.TaskService;
import com.FYP.FYP.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

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
        return "redirect:/projects/" + projectId;
    }

    @GetMapping("/details/{taskId}")
    public String showTaskDetails(@PathVariable Long taskId, Model model) {
        Optional<Task> taskOpt = taskService.getTaskById(taskId);

        if (taskOpt.isEmpty()) {
            return "redirect:/dashboard";
        }

        Task task = taskOpt.get();
        List<User> users = userService.getAllUsers();

        model.addAttribute("task", task);
        model.addAttribute("users", users);
        return "tasks/details";
    }

    @PostMapping("/update/{taskId}")
    public String updateTask(@PathVariable Long taskId,
                             @RequestParam String description,
                             @RequestParam TaskStatus status,
                             @RequestParam Long assignedTo) {
        taskService.updateTask(taskId, description, status, assignedTo);

        Optional<Task> taskOpt = taskService.getTaskById(taskId);
        if (taskOpt.isPresent()) {
            return "redirect:/tasks/details/" + taskId;
        }
        return "redirect:/dashboard";
    }
}
