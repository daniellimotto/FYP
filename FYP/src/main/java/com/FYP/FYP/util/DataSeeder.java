package com.FYP.FYP.util;

import com.FYP.FYP.model.*;
import com.FYP.FYP.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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
                    List<ChatMessage> chatMessages = createChatsForTask(task, users, project.getName());
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

        Project project1 = new Project();
        project1.setName("Website Redesign");
        project1.setDescription("Redesign and rebuild the company website with modern technologies");
        project1.setTeam(team);
        projects.add(projectRepository.save(project1));

        Project project2 = new Project();
        project2.setName("Mobile App Development");
        project2.setDescription("Develop a mobile app for both Android and iOS platforms");
        project2.setTeam(team);
        projects.add(projectRepository.save(project2));

        return projects;
    }

    private List<Task> createTasksForProject(Project project, List<User> users) {
        List<Task> tasks = new ArrayList<>();
        List<String> taskTitles = Arrays.asList("Design Mockups", "Frontend Development", "Backend Implementation");

        for (String taskTitle : taskTitles) {
            Task task = new Task();
            task.setTitle(taskTitle);
            task.setDescription("Detailed description for " + taskTitle);
            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setDueDate(getRandomFutureDate(30, 60));
            task.setProject(project);
            task.setAssignedTo(users.get(new Random().nextInt(users.size())));

            tasks.add(taskRepository.save(task));
        }

        return tasks;
    }

    private List<ChatMessage> createChatsForTask(Task task, List<User> users, String projectName) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        List<String> taskChats = new ArrayList<>();
        int messageCount = 0;

        if (task.getTitle().equals("Design Mockups")) {
            messageCount = 5;
            taskChats = Arrays.asList(
                    "Has anyone started working on the design yet?",
                    "I’ve created some initial mockups. Will share them soon.",
                    "Let’s plan a design review meeting this week.",
                    "I like the direction we’re going with this design.",
                    "Should we use more vibrant colors for this?"
            );
        } else if (task.getTitle().equals("Frontend Development")) {
            messageCount = 10;
            taskChats = Arrays.asList(
                    "Let's finalize the layout for the homepage.",
                    "We need to review the color scheme for the buttons.",
                    "Can you confirm the typography settings for headings?",
                    "Should we use more whitespace around the content?",
                    "How are we handling responsiveness on mobile?",
                    "We need to implement the user authentication page.",
                    "The homepage is almost ready for a review.",
                    "Let's check if all images are properly optimized for mobile.",
                    "Have you completed the navigation bar design?",
                    "The final frontend layout is almost done!"
            );
        } else if (task.getTitle().equals("Backend Implementation")) {
            messageCount = 20;
            taskChats = Arrays.asList(
                    "The backend APIs need to support user login and registration.",
                    "We should use JWT tokens for authentication.",
                    "Do we need to implement a session management system?",
                    "The database schema needs to be finalized first.",
                    "Are we using MySQL for the database?",
                    "The server-side validation is almost done.",
                    "Do we need to implement payment integration?",
                    "Let’s schedule a database migration this week.",
                    "We need to implement a file upload feature for the user profile.",
                    "The API endpoints are ready for testing.",
                    "We need to finalize the API documentation.",
                    "Has the API been tested in a staging environment?",
                    "I’m working on resolving some security concerns with the backend.",
                    "Do we need to implement caching for faster performance?",
                    "Let’s review the logging system for debugging purposes.",
                    "Are all endpoints RESTful and conform to the API standards?",
                    "Can we optimize the database queries for better performance?",
                    "Let’s test the backend for stress under high traffic.",
                    "We should discuss error handling and response codes.",
                    "The backend is almost complete, but some testing is still needed."
            );
        }

        if (projectName.equals("Mobile App Development")) {
            if (task.getTitle().equals("Design Mockups")) {
                taskChats = Arrays.asList(
                        "We need to finalize the UI design for the mobile app.",
                        "Le client veut que l'application ait un design moderne et épuré.",
                        "Can we add more icons to the mobile interface?",
                        "Nous devons revoir l'intégration API sur Android et iOS.",
                        "我们需要对APP进行全面的性能优化。"
                );
            }
            if (task.getTitle().equals("Frontend Development")) {
                taskChats = Arrays.asList(
                        "Let's integrate the APIs into the mobile app soon.",
                        "Pouvons-nous ajouter plus d'espace entre les sections ?",
                        "How are we handling responsiveness on mobile?",
                        "Le client a mentionné qu'il voulait une approche plus minimaliste.",
                        "我喜欢这个设计方向。",
                        "Let's finalize the color palette by the end of the week.",
                        "Finalisons la palette de couleurs d'ici la fin de la semaine.",
                        "We should test the navigation bar.",
                        "是否需要为用户添加多语言支持功能？",
                        "Can we add more whitespace between sections?"
                );
            }
            if (task.getTitle().equals("Backend Implementation")) {
                taskChats = Arrays.asList(
                        "Let's implement JWT authentication.",
                        "Le schéma de base de données doit être finalisé.",
                        "The API endpoints are ready for testing.",
                        "我们需要实施支付集成。",
                        "We need to test the performance of the backend.",
                        "Can we optimize the database queries?",
                        "Les utilisateurs doivent pouvoir se connecter avec leurs comptes.",
                        "我们需要实现文件上传功能。",
                        "Final database migration needs to happen this week.",
                        "数据库架构设计已经完成。",
                        "Shall we add two-factor authentication?",
                        "La validation côté serveur est presque terminée.",
                        "We should finalize the backend architecture soon.",
                        "将进行压力测试以确保系统能够处理大量流量。",
                        "Have we reviewed the system for security vulnerabilities?",
                        "Do we need caching for better backend performance?",
                        "这个功能应该尽快完成。",
                        "Les erreurs doivent être gérées correctement dans les API.",
                        "The backend implementation is almost done.",
                        "待处理的数据库迁移工作应尽快完成。"
                );
            }
        }

        Date startDate = new Date(System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000));
        long startTime = startDate.getTime();
        long endTime = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i++) {
            String message = taskChats.get(i);
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

    private Date getRandomFutureDate(int minDays, int maxDays) {
        LocalDate today = LocalDate.now();
        int daysToAdd = minDays + new Random().nextInt(maxDays - minDays);
        LocalDate futureDate = today.plusDays(daysToAdd);
        return Date.from(futureDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
