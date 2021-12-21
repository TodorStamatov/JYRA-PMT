package course.spring.jyra.web.rest;

import course.spring.jyra.dao.ProjectRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectControllerREST {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectControllerREST(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    @GetMapping("/{projectId}")
    public Project getProjectById(@PathVariable String projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException(String.format("Project with ID=%s not found.", projectId)));
    }


}
