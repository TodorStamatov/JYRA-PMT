package course.spring.jyra.web.rest;

import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.exception.InvalidClientDataException;
import course.spring.jyra.model.Board;
import course.spring.jyra.model.ErrorResponse;
import course.spring.jyra.model.Project;
import course.spring.jyra.service.*;
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
    private final ProjectResultService projectResultService;
    private final SprintService sprintService;
    private final BoardService boardService;
    private final TaskService taskService;
    private final TaskResultService taskResultService;
    private final SprintResultService sprintResultService;

    @Autowired
    public ProjectControllerREST(ProjectService projectService, ProjectResultService projectResultService, SprintService sprintService, BoardService boardService, TaskService taskService, TaskResultService taskResultService, SprintResultService sprintResultService) {
        this.projectService = projectService;
        this.projectResultService = projectResultService;
        this.sprintService = sprintService;
        this.boardService = boardService;
        this.taskService = taskService;
        this.taskResultService = taskResultService;
        this.sprintResultService = sprintResultService;
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
        Project project = projectService.findById(projectId);

        // prepare the project for deletion
        if (project.getProjectResultId() != null) {
            projectResultService.deleteById(project.getProjectResultId());
        }

        if (project.getCurrentSprintId() != null) {
            sprintService.deleteById(project.getCurrentSprintId());
            Board board = boardService.findAll().stream().filter(b -> b.getSprintId().equals(project.getCurrentSprintId())).findFirst().orElseThrow(() -> new EntityNotFoundException(String.format("Board for sprint with ID=%s not found.", project.getCurrentSprintId())));
            boardService.deleteById(board.getId());
        }

        for (String sprintResultId : project.getPreviousSprintResultsIds()) {
            String sprintId = sprintResultService.findById(sprintResultId).getSprintId();
            sprintResultService.deleteById(sprintResultId);
            sprintService.deleteById(sprintId);
        }

        for (String taskId : project.getTasksBacklogIds()) {
            if (taskService.findById(taskId).getTaskResultId() != null) {
                taskResultService.deleteById(taskService.findById(taskId).getTaskResultId());
            }
            taskService.deleteById(taskId);
        }

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
