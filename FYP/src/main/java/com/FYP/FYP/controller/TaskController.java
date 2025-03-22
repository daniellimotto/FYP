package com.FYP.FYP.controller;

import com.FYP.FYP.model.Task;
import com.FYP.FYP.model.TaskStatus;
import com.FYP.FYP.model.ChatMessage;
import com.FYP.FYP.model.User;
import com.FYP.FYP.model.ChatSummary;
import com.FYP.FYP.service.ChatService;
import com.FYP.FYP.service.TaskService;
import com.FYP.FYP.service.UserService;
import com.FYP.FYP.service.ChatSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatSummaryService chatSummaryService;

    @GetMapping("/details/{taskId}")
    public String showTaskDetails(@PathVariable int taskId, Model model) {
        Optional<Task> taskOpt = taskService.getTaskById(taskId);
        if (taskOpt.isEmpty()) {
            return "redirect:/dashboard";
        }

        Task task = taskOpt.get();
        model.addAttribute("task", task);
        return "tasks/details";
    }

    @GetMapping("/create/{projectId}")
    public String showCreateTaskForm(@PathVariable int projectId, Model model) {
        model.addAttribute("projectId", projectId);
        return "tasks/create";
    }

    @PostMapping("/create/{projectId}")
    public String createTask(@PathVariable int projectId,
                            @RequestParam String title,
                            @RequestParam String description,
                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dueDate) {
        taskService.createTask(title, description, dueDate, projectId);
        return "redirect:/projects/" + projectId;
    }

    @GetMapping("/edit/{taskId}")
    public String editTask(@PathVariable int taskId, Model model) {
        Optional<Task> taskOpt = taskService.getTaskById(taskId);
        if (taskOpt.isEmpty()) {
            return "redirect:/dashboard";
        }

        Task task = taskOpt.get();
        List<User> users = userService.getAllUsers();

        model.addAttribute("task", task);
        model.addAttribute("users", users);
        return "tasks/edit";
    }

    @PostMapping("/update/{taskId}")
    public String updateTask(@PathVariable int taskId,
                            @RequestParam String description,
                            @RequestParam TaskStatus status,
                            @RequestParam int assignedTo,
                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dueDate) {
        taskService.updateTask(taskId, description, status, assignedTo, dueDate);
        return "redirect:/tasks/details/" + taskId;
    }

    @PostMapping("/delete/{taskId}")
    public String deleteTask(@PathVariable int taskId) {
        Optional<Task> taskOpt = taskService.getTaskById(taskId);
        if (taskOpt.isPresent()) {
            int projectId = taskOpt.get().getProject().getId();
            taskService.deleteTask(taskId);
            return "redirect:/projects/" + projectId;
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/chat/{taskId}")
    public String showChat(@PathVariable int taskId, Model model) {
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        Optional<Task> taskOpt = taskService.getTaskById(taskId);
        if (taskOpt.isEmpty()) {
            return "redirect:/dashboard";
        }

        Task task = taskOpt.get();
        List<ChatMessage> messages = chatService.getMessagesByTask(taskId);
        
        ChatSummary chatSummary = chatSummaryService.getOrCreateSummary(task);
        
        if (chatSummaryService.shouldUpdateSummary(task)) {
            chatSummary = chatSummaryService.updateSummary(task);
        }
        
        model.addAttribute("messages", messages);
        model.addAttribute("taskId", taskId);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("task", task);
        model.addAttribute("chatSummary", chatSummary);
        
        return "tasks/chat";
    }

    @PostMapping("/chat/{taskId}")
    public String sendMessage(@PathVariable int taskId,
                              @RequestParam String message) {
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        
        chatService.saveMessage(taskId, loggedInUser, message);
        return "redirect:/tasks/chat/" + taskId;
    }

    @PostMapping("/summary/{taskId}/refresh")
    @ResponseBody
    public Map<String, String> refreshSummary(@PathVariable int taskId) {
        Optional<Task> taskOpt = taskService.getTaskById(taskId);
        if (taskOpt.isEmpty()) {
            return Map.of("error", "Task not found");
        }
        
        Task task = taskOpt.get();
        ChatSummary summary = chatSummaryService.updateSummary(task);
        
        String formattedSummary = summary.getSummary().replace("\n", "<br>");
        
        return Map.of("summary", formattedSummary);
    }
}
