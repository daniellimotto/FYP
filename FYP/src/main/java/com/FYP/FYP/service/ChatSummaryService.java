package com.FYP.FYP.service;

import com.FYP.FYP.model.ChatMessage;
import com.FYP.FYP.model.ChatSummary;
import com.FYP.FYP.model.Task;
import com.FYP.FYP.repository.ChatRepository;
import com.FYP.FYP.repository.ChatSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatSummaryService {

    @Autowired
    private ChatSummaryRepository chatSummaryRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatGptService chatGptService;

    public ChatSummary getOrCreateSummary(Task task) {
        Optional<ChatSummary> existingSummary = chatSummaryRepository.findByTask(task);
        
        if (existingSummary.isPresent()) {
            return existingSummary.get();
        } else {
            List<ChatMessage> messages = chatRepository.findByTaskOrderBySentAtAsc(task);
            int currentMessageCount = messages.size();
            
            ChatSummary newSummary = new ChatSummary();
            newSummary.setTask(task);
            newSummary.setSummary("No summary generated yet.");
            newSummary.setMessageCount(currentMessageCount);
            return chatSummaryRepository.save(newSummary);
        }
    }

    public ChatSummary updateSummary(Task task) {
        List<ChatMessage> messages = chatRepository.findByTaskOrderBySentAtAsc(task);
        
        if (messages.isEmpty()) {
            ChatSummary summary = getOrCreateSummary(task);
            summary.setSummary("No chat messages to summarize.");
            summary.setMessageCount(0);
            return chatSummaryRepository.save(summary);
        }
        
        List<String> messageTexts = messages.stream()
            .map(msg -> String.format("%s (%s): %s", 
                msg.getUser().getEmail(), 
                msg.getSentAt(), 
                msg.getMessage()))
            .collect(Collectors.toList());
        
        String assignedTo = task.getAssignedTo() != null ? task.getAssignedTo().getEmail() : "Unassigned";
        
        String summaryText = chatGptService.generateChatSummary(
            task.getTitle(), 
            task.getDescription(), 
            task.getDueDate(),
            assignedTo, 
            messageTexts
        );
        
        ChatSummary summary = getOrCreateSummary(task);
        summary.setSummary(summaryText);
        summary.setLastUpdated(new Date());
        summary.setMessageCount(messages.size());
        
        return chatSummaryRepository.save(summary);
    }

    public boolean shouldUpdateSummary(Task task) {
        Optional<ChatSummary> existingSummary = chatSummaryRepository.findByTask(task);
        
        if (existingSummary.isEmpty()) {
            return true;
        }
        
        ChatSummary summary = existingSummary.get();
        Date lastUpdated = summary.getLastUpdated();
        
        if (lastUpdated == null) {
            return true;
        }
        
        List<ChatMessage> messages = chatRepository.findByTaskOrderBySentAtAsc(task);
        if (messages.isEmpty()) {
            return false;
        }
        
        Date latestMessageDate = messages.get(messages.size() - 1).getSentAt();
        
        return latestMessageDate.after(lastUpdated);
    }
}
