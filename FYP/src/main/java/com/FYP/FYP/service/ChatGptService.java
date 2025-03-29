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
        
        contextBuilder.append(
            "\nYou are an assistant that summarizes multilingual task discussions into concise English.\n\n" +
            "Instructions:\n" +
            "1. First, translate any non-English chat messages into natural-sounding English.\n" +
            "2. Summarize the entire discussion by focusing on **key points, decisions, and actions**.\n" +
            "3. **Create topic headers dynamically** based on the content of the discussion. Group related messages under these topics.\n" +
            "4. **Do not include email addresses**—only use their **first name** (e.g., 'Bob', 'Jane').\n" +
            "5. **Avoid unnecessary details** or repeated information that doesn’t contribute to decision-making or the action plan.\n" +
            "6. Ensure that any **important suggestions, decisions, or actions** are captured, including scheduling, planning, or clarifications.\n" +
            "7. Group similar ideas under topics based on their content, and make sure each bullet point includes the person's contribution.\n\n" +
            "Summary Format:\n" +
            "====================\n" +
            "Discussion Summary:\n" +
            "• <b>[Generated Topic]:</b>\n" +
            "    - [Contribution from a person or action item].\n" +
            "    - [Another contribution under the same topic].\n" +
            "• <b>[Next Generated Topic]:</b>\n" +
            "    - [Another person's input].\n" +
            "    - [Any other important information].\n\n" +
            "Example:\n" +
            "====================\n" +
            "Discussion Summary:\n" +
            "• <b>Design Review:</b>\n" +
            "    - Bob created the initial design mockups and plans to share them soon.\n" +
            "    - Jane suggested simplifying the design further.\n" +
            "    - Mary mentioned that the design is approved for implementation.\n" +
            "• <b>UI Adjustments:</b>\n" +
            "    - Bob recommended considering a mobile-first design approach.\n" +
            "    - Mary proposed simplifying the user interface.\n" +
            "• <b>Scheduling:</b>\n" +
            "    - Bob suggested scheduling a design review meeting this week.\n" +
            "    - Jane mentioned a possible time slot for the meeting.\n\n" +
            "Please follow this format strictly. Translate first, then summarize. Focus on **key actions**, **decisions**, and **next steps**."
        );
                        
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
        
        if (content.contains("• ") && content.contains("\n")) {
            return content;
        }
        
        int headerEnd = content.indexOf("•");
        if (headerEnd == -1) {
            headerEnd = content.indexOf("-");
        }
        
        if (headerEnd > 0) {
            String header = content.substring(0, headerEnd).trim();
            String bulletPoints = content.substring(headerEnd);
            
            bulletPoints = bulletPoints.replace("• ", "\n• ")
                                      .replace(" •", "\n•")
                                      .replace(" - ", "\n• ")
                                      .replace("- ", "\n• ");
            
            if (!bulletPoints.startsWith("\n")) {
                bulletPoints = "\n" + bulletPoints;
            }
            
            return header + bulletPoints;
        }
        
        String[] parts = content.split("\\. ");
        if (parts.length >= 4) {
            StringBuilder formatted = new StringBuilder();
            formatted.append(parts[0]).append(". ")
                     .append(parts[1]).append(". ")
                     .append(parts[2]).append(".\n\n");
            
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
