package com.FYP.FYP.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

@Service
public class ChatGptService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "https://api.openai.com/v1/chat/completions";

    public String generateChatSummary(String taskTitle, String taskDescription, Date taskDeadline, String taskAssignedTo, List<String> messages) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("Task: ").append(taskTitle)
            .append(". ")
            .append("Deadline: ").append(taskDeadline != null ? new SimpleDateFormat("MMM d, yyyy").format(taskDeadline) : "Not set")
            .append(". ")
            .append("Assigned to: ").append(taskAssignedTo != null ? taskAssignedTo : "Unassigned")
            .append(".\n\n");
        
        if (taskDescription != null && !taskDescription.isEmpty()) {
            contextBuilder.append("Description: ").append(taskDescription).append("\n\n");
        }
        
        contextBuilder.append("Chat History:\n");
        
        for (String message : messages) {
            contextBuilder.append("- ").append(message).append("\n");
        }
        
        contextBuilder.append("\nPlease provide a concise summary of this task chat discussion. " +
                "Format your response with the following structure:\n\n" +
                "1. First line: The task header in this exact format: \"Task: [title]. Deadline: [deadline]. Assigned to: [assignee].\"\n" +
                "2. Second line: Leave blank\n" +
                "3. Then provide the discussion summary with bullet points, where each point attributes ideas to specific people.\n\n" +
                "For example:\n" +
                "Task: Database setup. Deadline: May 12, 2025. Assigned to: john.doe@example.com.\n\n" +
                "• Jane suggested setting up a MySQL instance first\n" +
                "• Bob proposed documenting the schema before implementation\n" +
                "• Mary agreed to review the final database design\n\n" +
                "Focus on main points, decisions made, and action items mentioned.");

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant that summarizes task-related discussions. Your summaries should be structured with a clear task header followed by bullet points that attribute ideas to their originators.");

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", contextBuilder.toString());

        requestBody.put("messages", List.of(systemMessage, userMessage));
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            Map responseBody = response.getBody();
            
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    String content = message.get("content");
                    
                    return formatSummaryContent(content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "Failed to generate summary.";
    }

    private String formatSummaryContent(String content) {
        if (content == null || content.isEmpty()) {
            return "No summary available.";
        }
        
        // First, check if the content already has proper formatting
        if (content.contains("• ") && content.contains("\n")) {
            return content; // Already properly formatted
        }
        
        // Extract the task header
        int headerEnd = content.indexOf("•");
        if (headerEnd == -1) {
            headerEnd = content.indexOf("-");
        }
        
        if (headerEnd > 0) {
            String header = content.substring(0, headerEnd).trim();
            String bulletPoints = content.substring(headerEnd);
            
            // Convert bullet points
            bulletPoints = bulletPoints.replace("• ", "\n• ")
                                      .replace(" •", "\n•")
                                      .replace(" - ", "\n• ")
                                      .replace("- ", "\n• ");
            
            // Ensure there's a blank line between header and bullets
            if (!bulletPoints.startsWith("\n")) {
                bulletPoints = "\n" + bulletPoints;
            }
            
            return header + bulletPoints;
        }
        
        // If we can't identify the structure, use a simple approach
        String[] parts = content.split("\\. ");
        if (parts.length >= 4) {
            StringBuilder formatted = new StringBuilder();
            // Format the header
            formatted.append(parts[0]).append(". ")
                     .append(parts[1]).append(". ")
                     .append(parts[2]).append(".\n\n");
            
            // Format the summary section
            String summary = parts[3];
            String[] sentences = summary.split("\\. ");
            for (String sentence : sentences) {
                if (!sentence.trim().isEmpty()) {
                    formatted.append("• ").append(sentence.trim()).append("\n");
                }
            }
            
            return formatted.toString();
        }
        
        return content;
    }
}
