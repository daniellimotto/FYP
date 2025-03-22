package com.FYP.FYP.util;

import com.FYP.FYP.model.*;
import com.FYP.FYP.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
public class DataSeeder {

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private ChatSummaryRepository chatSummaryRepository;
    
    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            if (projectRepository.count() > 0) {
                System.out.println("Database already seeded. Skipping data initialization.");
                return;
            }
            
            System.out.println("Seeding database with sample data...");
            
            Team team = getOrCreateTeam();
            
            List<User> users = getOrCreateUsers(team);
            
            List<Project> projects = createProjects(team);
            
            Map<Task, List<ChatMessage>> tasksWithChats = new HashMap<>();
            for (Project project : projects) {
                List<Task> tasks = createTasksForProject(project, users);
                
                for (Task task : tasks) {
                    List<ChatMessage> chatMessages = createChatsForTask(task, users);
                    tasksWithChats.put(task, chatMessages);
                }
            }
            
            System.out.println("Database seeding completed!");
        };
    }

    private Team getOrCreateTeam() {
        Optional<Team> existingTeam = teamRepository.findById(1);
        if (existingTeam.isPresent()) {
            return existingTeam.get();
        }
        
        Team team = new Team();
        team.setName("Development Team");
        return teamRepository.save(team);
    }
    
    private List<User> getOrCreateUsers(Team team) {
        List<User> users = new ArrayList<>();
        List<String> userEmails = Arrays.asList(
            "john.doe@example.com",
            "jane.smith@example.com",
            "bob.johnson@example.com",
            "mary.williams@example.com"
        );
        
        for (String email : userEmails) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setPassword("$2a$10$A8PY3HhFMQ.CYhHdmXATN.y4SgjBI8c5x1OLCz6mXGJXXKHlOG/9C");
                user.setTeam(team);
                user = userRepository.save(user);
            }
            users.add(user);
        }
        
        return users;
    }
    
    private List<Project> createProjects(Team team) {
        List<Project> projects = new ArrayList<>();
        
        String[] projectNames = {"Website Redesign", "Mobile App Development", "Cloud Migration"};
        String[] projectDescriptions = {
            "Redesign and rebuild the company website with modern technologies",
            "Develop a mobile app for both Android and iOS platforms",
            "Migrate all on-premises systems to cloud infrastructure"
        };
        
        for (int i = 0; i < projectNames.length; i++) {
            Project project = new Project();
            project.setName(projectNames[i]);
            project.setDescription(projectDescriptions[i]);
            project.setTeam(team);
            projects.add(projectRepository.save(project));
        }
        
        return projects;
    }
    
    private List<Task> createTasksForProject(Project project, List<User> users) {
        List<Task> tasks = new ArrayList<>();
        
        List<String> taskTitles;
        List<String> taskDescriptions;
        
        if (project.getName().contains("Website")) {
            taskTitles = Arrays.asList(
                "Design mockups", 
                "Frontend development", 
                "Backend implementation", 
                "Database setup", 
                "Testing and QA"
            );
            taskDescriptions = Arrays.asList(
                "Create design mockups for all main pages",
                "Implement frontend using React and Bootstrap",
                "Develop backend APIs and services",
                "Set up and configure the database schema",
                "Perform comprehensive testing and quality assurance"
            );
        } else if (project.getName().contains("Mobile")) {
            taskTitles = Arrays.asList(
                "UI/UX design", 
                "iOS development", 
                "Android development", 
                "API integration", 
                "App store submission"
            );
            taskDescriptions = Arrays.asList(
                "Design user interface and experience for mobile platforms",
                "Develop the iOS version of the application",
                "Develop the Android version of the application",
                "Integrate with backend APIs and services",
                "Prepare and submit the app to Apple App Store and Google Play Store"
            );
        } else {
            taskTitles = Arrays.asList(
                "Infrastructure assessment", 
                "Migration planning", 
                "Data transfer", 
                "System configuration", 
                "Post-migration testing"
            );
            taskDescriptions = Arrays.asList(
                "Assess current infrastructure and requirements",
                "Develop a detailed migration plan and timeline",
                "Transfer data from on-premises to cloud storage",
                "Configure cloud services and systems",
                "Test all systems after migration is complete"
            );
        }
        
        for (int i = 0; i < taskTitles.size(); i++) {
            Task task = new Task();
            task.setTitle(taskTitles.get(i));
            task.setDescription(taskDescriptions.get(i));
            task.setStatus(getRandomStatus());
            task.setDueDate(getRandomFutureDate(30, 60));
            task.setProject(project);
            
            // Randomly assign to a user
            if (Math.random() > 0.2) { 
                task.setAssignedTo(users.get(new Random().nextInt(users.size())));
            }
            
            tasks.add(taskRepository.save(task));
        }
        
        return tasks;
    }
    
    private List<ChatMessage> createChatsForTask(Task task, List<User> users) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        int messageCount = 4 + new Random().nextInt(7);
        
        List<String> messageTemplates;
        
        if (task.getTitle().contains("design") || task.getTitle().contains("Design")) {
            messageTemplates = Arrays.asList(
                "Has anyone started working on the design yet?",
                "I've created some initial mockups. Will share them soon.",
                "Let's plan a design review meeting this week.",
                "I like the direction we're going with this design.",
                "Should we use more vibrant colors for this?",
                "Can we add more whitespace between sections?",
                "The client mentioned they want a more minimalist approach.",
                "Typography looks great, but let's reconsider the font size on mobile.",
                "I'll prepare the final designs by Friday.",
                "Designs approved! We can move to implementation now."
            );
        } else if (task.getTitle().contains("develop") || task.getTitle().contains("implement")) {
            messageTemplates = Arrays.asList(
                "Which framework are we using for this?",
                "I've set up the basic project structure.",
                "Having some issues with dependency conflicts.",
                "Can someone review my PR when they get a chance?",
                "The build is failing on the CI server.",
                "Fixed the issue with the authentication module.",
                "Let's discuss how to handle error states.",
                "Performance seems slow on large datasets.",
                "All unit tests are passing now.",
                "Code review comments addressed and merged to main."
            );
        } else {
            messageTemplates = Arrays.asList(
                "When is the deadline for this task?",
                "I can start working on this next week.",
                "Does anyone have more details about the requirements?",
                "Let's have a quick call to clarify some points.",
                "I've documented everything in the wiki.",
                "This task is more complex than we initially thought.",
                "Can we break this down into smaller subtasks?",
                "I'll need help from the backend team for this.",
                "Almost done, just need to run final checks.",
                "Task completed and ready for review!"
            );
        }
        
        Date startDate = new Date(System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000));
        long startTime = startDate.getTime();
        long endTime = System.currentTimeMillis();
        
        for (int i = 0; i < messageCount; i++) {
            String message = messageTemplates.get(i % messageTemplates.size());
            User user = users.get(new Random().nextInt(users.size()));
            
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setTask(task);
            chatMessage.setUser(user);
            chatMessage.setMessage(message);
            
            long randomTime = startTime + (long) ((endTime - startTime) * ((double) i / messageCount));
            chatMessage.setSentAt(new Date(randomTime));
            
            chatMessages.add(chatRepository.save(chatMessage));
        }
        
        createChatSummary(task, chatMessages);
        
        return chatMessages;
    }
    
    private void createChatSummary(Task task, List<ChatMessage> chatMessages) {
        ChatSummary summary = new ChatSummary();
        summary.setTask(task);
        summary.setMessageCount(chatMessages.size());
        
        String summaryText = "Discussion about " + task.getTitle() + " with " + 
                             chatMessages.size() + " messages. " +
                             "Team members are " + (task.getStatus() == TaskStatus.COMPLETED ? 
                                                   "reporting completion of the task." : 
                                                   "actively working on this task.");
        
        summary.setSummary(summaryText);
        summary.setLastUpdated(new Date());
        
        chatSummaryRepository.save(summary);
    }
    
    private TaskStatus getRandomStatus() {
        TaskStatus[] statuses = TaskStatus.values();
        return statuses[new Random().nextInt(statuses.length)];
    }
    
    private Date getRandomFutureDate(int minDays, int maxDays) {
        LocalDate today = LocalDate.now();
        int daysToAdd = minDays + new Random().nextInt(maxDays - minDays);
        LocalDate futureDate = today.plusDays(daysToAdd);
        return Date.from(futureDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
} 