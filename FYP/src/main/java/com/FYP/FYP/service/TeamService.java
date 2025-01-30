package com.FYP.FYP.service;

import com.FYP.FYP.model.Team;
import com.FYP.FYP.model.User;
import com.FYP.FYP.repository.TeamRepository;
import com.FYP.FYP.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * Creates a new team.
     */
    public Team createTeam(String name) {
        Team team = new Team();
        team.setName(name);
        return teamRepository.save(team);
    }

    /**
     * Gets all teams.
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Adds the logged-in user to a team.
     */
    public boolean joinTeam(Long teamId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        User loggedInUser = userService.getLoggedInUser();

        if (teamOpt.isPresent() && loggedInUser != null) {
            Team team = teamOpt.get();
            loggedInUser.setTeam(team);
            userRepository.save(loggedInUser);
            return true;
        }
        return false;
    }
}
