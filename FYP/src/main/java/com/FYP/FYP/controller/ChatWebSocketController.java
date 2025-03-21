package com.FYP.FYP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.FYP.FYP.model.ChatMessage;
import com.FYP.FYP.model.ChatSummary;
import com.FYP.FYP.model.Task;
import com.FYP.FYP.model.User;
import com.FYP.FYP.service.ChatService;
import com.FYP.FYP.repository.TaskRepository;
import com.FYP.FYP.repository.UserRepository;
import com.FYP.FYP.service.ChatSummaryService;

import java.util.Date;

@Controller
public class ChatWebSocketController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ChatSummaryService chatSummaryService;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getTask() == null || chatMessage.getUser() == null) {
            throw new IllegalArgumentException("Task or User is missing in the message payload");
        }

        int taskId = chatMessage.getTask().getId();
        int userId = chatMessage.getUser().getId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        ChatMessage savedMessage = chatService.saveMessage(taskId, user, chatMessage.getMessage());

        ChatSummary summary = chatSummaryService.getOrCreateSummary(task);
        
        messagingTemplate.convertAndSend("/topic/chat/" + taskId, savedMessage);

        return savedMessage;
    }
}
