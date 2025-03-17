package com.FYP.FYP.service;

import com.FYP.FYP.model.ChatMessage;
import com.FYP.FYP.model.Task;
import com.FYP.FYP.model.User;
import com.FYP.FYP.repository.ChatRepository;
import com.FYP.FYP.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<ChatMessage> getMessagesByTask(int taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        return taskOpt.map(chatRepository::findByTaskOrderBySentAtAsc).orElse(List.of());
    }

    public ChatMessage saveMessage(int taskId, User user, String message) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        System.out.println("saving message in service");
        if (taskOpt.isPresent() && user != null) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setTask(taskOpt.get());
            chatMessage.setUser(user);
            chatMessage.setMessage(message);
            return chatRepository.save(chatMessage);
        }
        return null;
    }    
}