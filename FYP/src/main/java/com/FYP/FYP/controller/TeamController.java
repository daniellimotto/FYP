package com.FYP.FYP.controller;

import com.FYP.FYP.model.Team;
import com.FYP.FYP.service.TeamService;
import com.FYP.FYP.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @GetMapping("/create")
    public String showCreateTeamForm(Model model) {
        model.addAttribute("team", new Team());
        return "teams/create";
    }

    @PostMapping("/create")
    public String createTeam(@RequestParam String name) {
        teamService.createTeam(name);
        return "redirect:/dashboard";
    }

    @GetMapping("/join")
    public String showJoinTeamForm(Model model) {
        List<Team> teams = teamService.getAllTeams();
        model.addAttribute("teams", teams);
        return "teams/join";
    }

    @PostMapping("/join")
    public String joinTeam(@RequestParam Long teamId) {
        boolean success = teamService.joinTeam(teamId);
        return success ? "redirect:/dashboard" : "redirect:/teams/join?error=true";
    }
}
