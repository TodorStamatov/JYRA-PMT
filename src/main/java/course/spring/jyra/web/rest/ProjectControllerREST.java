package course.spring.jyra.web.rest;

import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.exception.InvalidClientDataException;
import course.spring.jyra.model.ErrorResponse;
import course.spring.jyra.model.Project;
import course.spring.jyra.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectControllerREST {
    private final ProjectService projectService;

    @Autowired
    public ProjectControllerREST(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<Project> getProjects() {
        return projectService.findAll();
    }

    @GetMapping("/{projectId}")
    public Project getProjectById(@PathVariable String projectId) {
        return projectService.findById(projectId);
    }

    @PostMapping
    public ResponseEntity<Project> addProject(@RequestBody Project project) {
        Project created = projectService.create(project);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .pathSegment("{projectId}").buildAndExpand(created.getId()).toUri()).body(created);
    }

    @PutMapping("/{projectId}")
    public Project updateProject(@PathVariable String projectId, @RequestBody Project project) {
        if (!projectId.equals(project.getId()))
            throw new InvalidClientDataException(String.format("Project ID %s from URL doesn't match ID %s in Request body", projectId, project.getId()));
        return projectService.update(project);
    }

    @DeleteMapping("/{projectId}")
    public Project deleteProject(@PathVariable String projectId) {
        return projectService.deleteById(projectId);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException entityNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), entityNotFoundException.getMessage(), entityNotFoundException.toString()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInvalidClientData(InvalidClientDataException invalidClientDataException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), invalidClientDataException.getMessage(), invalidClientDataException.toString()));
    }
}
