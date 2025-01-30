package com.FYP.FYP.controller;

import com.FYP.FYP.model.Team;
import com.FYP.FYP.model.User;
import com.FYP.FYP.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              Model model) {
        boolean isValid = userService.validateUser(email, password);

        if (isValid) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        User loggedInUser = userService.getLoggedInUser();

        if (loggedInUser == null) {
            return "redirect:/login"; 
        }

        boolean isInTeam = loggedInUser.getTeam() != null;
        model.addAttribute("isInTeam", isInTeam);
        model.addAttribute("user", loggedInUser);

        if (isInTeam) {
            Team userTeam = loggedInUser.getTeam();
            List<?> projects = (userTeam.getProjects() != null) ? userTeam.getProjects() : List.of();
            model.addAttribute("projects", projects);
        }

        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/login";
    }
}
