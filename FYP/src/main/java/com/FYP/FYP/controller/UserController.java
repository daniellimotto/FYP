package com.FYP.FYP.controller;

import com.FYP.FYP.model.Team;
import com.FYP.FYP.model.User;
import com.FYP.FYP.model.Notification;
import com.FYP.FYP.repository.UserRepository;
import com.FYP.FYP.service.NotificationService;
import com.FYP.FYP.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

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
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) { 
            session.setAttribute("loggedInUser", userOpt.get());
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String confirmPassword,
                                 Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email already in use.");
            return "register";
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        userRepository.save(newUser);

        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
    
        if (sessionUser == null) {
            return "redirect:/login"; 
        }
    
        Optional<User> latestUserOpt = userRepository.findById(sessionUser.getId());
        if (latestUserOpt.isEmpty()) {
            return "redirect:/login"; 
        }
        User latestUser = latestUserOpt.get();
    
        session.setAttribute("loggedInUser", latestUser);
    
        boolean isInTeam = latestUser.getTeam() != null;
        model.addAttribute("isInTeam", isInTeam);
        model.addAttribute("user", latestUser);
    
        if (isInTeam) {
            Team userTeam = latestUser.getTeam();
            List<?> projects = (userTeam.getProjects() != null) ? userTeam.getProjects() : List.of();
            model.addAttribute("projects", projects);
        }
    
        List<Notification> notifications = notificationService.getUnreadNotifications(latestUser);
        model.addAttribute("notifications", notifications);
    
        return "dashboard";
    }    

    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/login";
    }
}
