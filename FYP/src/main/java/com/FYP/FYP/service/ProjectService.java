package com.FYP.FYP.service;

import com.FYP.FYP.model.Project;
import com.FYP.FYP.model.Team;
import com.FYP.FYP.repository.ProjectRepository;
import com.FYP.FYP.service.TeamService;
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
            Project project = new Project();
            project.setName(name);
            project.setDescription(description);
            project.setTeam(teamOpt.get());
            return projectRepository.save(project);
        }
        return null;
    }

    public List<Project> getProjectsByTeam(Long teamId) {
        return projectRepository.findByTeamId(teamId);
    }
}
