package course.spring.jyra.web.rest;

import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.exception.InvalidClientDataException;
import course.spring.jyra.model.ErrorResponse;
import course.spring.jyra.model.ProjectResult;
import course.spring.jyra.service.ProjectResultService;
import course.spring.jyra.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectResultControllerREST {
    private final ProjectResultService projectResultService;
    private final ProjectService projectService;

    @Autowired
    public ProjectResultControllerREST(ProjectResultService projectResultService, ProjectService projectService) {
        this.projectResultService = projectResultService;
        this.projectService = projectService;
    }

    @GetMapping("/project-results")
    public List<ProjectResult> getProjectResults() {
        return projectResultService.findAll();
    }

    @GetMapping("/{projectId}/project-result")
    public ProjectResult getResultsByProjectId(@PathVariable("{projectId}") String id) {
        return projectResultService.findByProject(id);
    }

    @PostMapping
    public ResponseEntity<ProjectResult> addProject(@RequestBody ProjectResult projectResult) {
        ProjectResult created = projectResultService.create(projectResult);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .pathSegment("{projectId}").buildAndExpand(created.getProjectId()).toUri()).body(created);
    }

    @PutMapping("/{projectId}/project-result")
    public ProjectResult updateProject(@PathVariable("projectId") String projectId, @RequestBody ProjectResult projectResult) {
        if (!projectId.equals(projectResult.getProjectId()))
            throw new InvalidClientDataException(String.format("Project ID %s from URL doesn't match ID %s in Request body", projectId, projectResult.getProjectId()));
        return projectResultService.update(projectResult);
    }

    @DeleteMapping("/{projectId}/project-result")
    public ProjectResult deleteProjectResult(@PathVariable("projectId") String id) {
        String deletedId = projectService.findById(id).getProjectResultId();
        return projectResultService.deleteById(deletedId);
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
