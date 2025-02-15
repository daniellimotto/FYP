package com.FYP.FYP.controller;

import com.FYP.FYP.model.Notification;
import com.FYP.FYP.model.User;
import com.FYP.FYP.service.NotificationService;
import com.FYP.FYP.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getNotifications(Model model) {
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Notification> notifications = notificationService.getUnreadNotifications(loggedInUser);
        model.addAttribute("notifications", notifications);
        model.addAttribute("user", loggedInUser);
        return "dashboard";
    }

    @PostMapping("/mark-read")
    public String markNotificationsAsRead() {
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        notificationService.markAllAsRead(loggedInUser);
        return "redirect:/dashboard";
    }
}
