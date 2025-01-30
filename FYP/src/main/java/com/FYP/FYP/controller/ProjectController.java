package com.FYP.FYP.controller;

import com.FYP.FYP.model.Project;
import com.FYP.FYP.model.Task;
import com.FYP.FYP.model.User;
import com.FYP.FYP.service.ProjectService;
import com.FYP.FYP.service.TaskService;
import com.FYP.FYP.service.UserService;
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

    @GetMapping
    public String listProjects(Model model) {
        User loggedInUser = userService.getLoggedInUser();

        if (loggedInUser == null || loggedInUser.getTeam() == null) {
            return "redirect:/dashboard";
        }

        List<Project> projects = projectService.getProjectsByTeam(loggedInUser.getTeam().getId());
        model.addAttribute("projects", projects);
        return "projects/list";
    }

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

        return "redirect:/projects";
    }

    @GetMapping("/{id}")
    public String showProjectDetails(@PathVariable Long id, Model model) {
        Optional<Project> projectOpt = projectService.getProjectById(id);

        if (projectOpt.isEmpty()) {
            return "redirect:/projects";
        }

        Project project = projectOpt.get();
        List<Task> tasks = taskService.getTasksByProject(id);

        model.addAttribute("project", project);
        model.addAttribute("tasks", tasks);

        return "projects/details";
    }
}
