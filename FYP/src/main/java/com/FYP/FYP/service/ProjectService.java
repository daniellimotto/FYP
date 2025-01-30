package com.FYP.FYP.service;

import com.FYP.FYP.model.Project;
import com.FYP.FYP.model.Team;
import com.FYP.FYP.repository.ProjectRepository;
import com.FYP.FYP.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamService teamService;

    public Project createProject(String name, String description, Long teamId) {
        Optional<Team> teamOpt = teamService.getTeamById(teamId);

        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();

            boolean exists = team.getProjects().stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(name));

            if (exists) {
                return null; 
            }

            Project project = new Project();
            project.setName(name);
            project.setDescription(description);
            project.setTeam(team);
            return projectRepository.save(project);
        }
        return null;
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> getProjectsByTeam(Long teamId) {
        return projectRepository.findByTeamId(teamId);
    }
}
