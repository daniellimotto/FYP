package com.FYP.FYP.controller;

import com.FYP.FYP.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Points to login.html in templates
    }

    @PostMapping("/login")
    public String handleLogin(
        @RequestParam("email") String email,
        @RequestParam("password") String password,
        Model model
    ) {
        boolean isValid = userService.validateUser(email, password);

        if (isValid) {
            return "redirect:/dashboard"; // Redirect to /dashboard
        } else {
            model.addAttribute("error", "Invalid email or password."); // Pass error to view
            return "login"; // Reload login page
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard"; // Points to dashboard.html in templates
    }
}
