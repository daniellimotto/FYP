package com.FYP.FYP.controller;

import com.FYP.FYP.model.Project;
import com.FYP.FYP.model.Task;
import com.FYP.FYP.model.User;
import com.FYP.FYP.service.ProjectService;
import com.FYP.FYP.service.TaskService;
import com.FYP.FYP.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HttpSession session;

    @GetMapping("/create")
    public String showCreateProjectForm(Model model) {
        model.addAttribute("project", new Project());
        return "projects/create";
    }

    @PostMapping("/create")
    public String createProject(@RequestParam String name,
                                @RequestParam String description,
                                Model model) {
        User loggedInUser = userService.getLoggedInUser();

        if (loggedInUser == null || loggedInUser.getTeam() == null) {
            return "redirect:/dashboard";
        }

        Project newProject = projectService.createProject(name, description, loggedInUser.getTeam().getId());

        if (newProject == null) {
            model.addAttribute("error", "A project with this name already exists in your team.");
            return "projects/create"; 
        }

        User updatedUser = userService.findById(loggedInUser.getId()).get();
        session.setAttribute("loggedInUser", updatedUser);

        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String showProjectDetails(@PathVariable int id, Model model) {
        Optional<Project> projectOpt = projectService.getProjectById(id);

        if (projectOpt.isEmpty()) {
            return "redirect:/dashboard";
        }

        Project project = projectOpt.get();
        List<Task> tasks = taskService.getTasksByProject(id);

        model.addAttribute("project", project);
        model.addAttribute("tasks", tasks);

        return "projects/details";
    }
}
