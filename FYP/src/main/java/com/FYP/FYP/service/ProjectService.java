package com.FYP.FYP.service;

import com.FYP.FYP.model.Project;
import com.FYP.FYP.model.Task;
import com.FYP.FYP.model.Team;
import com.FYP.FYP.repository.ProjectRepository;
import com.FYP.FYP.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.FYP.FYP.repository.TaskRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TaskRepository taskRepository;

    public Project createProject(String name, String description, int teamId) {
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

    public Optional<Project> getProjectById(int id) {
        return projectRepository.findById(id);
    }

    public List<Project> getProjectsByTeam(int teamId) {
        return projectRepository.findByTeamId(teamId);
    }

    @Transactional
    public boolean deleteProject(int projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            
            try {
                if (project.getTasks() != null && !project.getTasks().isEmpty()) {
                    List<Integer> taskIds = project.getTasks().stream()
                        .map(Task::getId)
                        .collect(Collectors.toList());
                    
                    for (Integer taskId : taskIds) {
                        taskRepository.deleteById(taskId);
                    }
                    
                    project.getTasks().clear();
                }
                
                if (project.getTeam() != null) {
                    Team team = project.getTeam();
                    team.getProjects().remove(project);
                    project.setTeam(null);
                }
                
                projectRepository.delete(project);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}
