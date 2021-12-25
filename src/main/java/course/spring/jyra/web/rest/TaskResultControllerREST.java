package course.spring.jyra.web.rest;


import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.exception.InvalidClientDataException;
import course.spring.jyra.model.ErrorResponse;
import course.spring.jyra.model.TaskResult;
import course.spring.jyra.service.TaskResultService;
import course.spring.jyra.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskResultControllerREST {
    private final TaskResultService taskResultService;
    private final TaskService taskService;

    @Autowired
    public TaskResultControllerREST(TaskResultService taskResultService, TaskService taskService) {
        this.taskResultService = taskResultService;
        this.taskService = taskService;
    }

    @GetMapping("/task-results")
    public List<TaskResult> getTaskResults() {
        return taskResultService.findAll();
    }

    @GetMapping("/{taskId}/task-result")
    public TaskResult getResultsByTaskId(@PathVariable String taskId) {
        return taskResultService.findById(taskId);
    }

    @PostMapping("/{taskId}/task-result")
    public ResponseEntity<TaskResult> addTaskResult(@PathVariable String taskId, @RequestBody TaskResult taskResult) {
        if (!taskId.equals(taskResult.getTask().getId()))
            throw new InvalidClientDataException(String.format("Task ID %s from URL doesn't match ID %s in Request body", taskId, taskResult.getTask().getId()));
        TaskResult created = taskResultService.create(taskResult);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .pathSegment("{taskId}").buildAndExpand(created.getTask().getId()).toUri()).body(created);
    }

    @PutMapping("/{taskId}/task-result")
    public TaskResult updateTask(@PathVariable String taskId, @RequestBody TaskResult taskResult) {
        if (!taskId.equals(taskResult.getTask().getId()))
            throw new InvalidClientDataException(String.format("Task ID %s from URL doesn't match ID %s in Request body", taskId, taskResult.getTask().getId()));
        return taskResultService.update(taskResult);
    }

    @DeleteMapping("/{taskId}/task-result")
    public TaskResult deleteTaskResult(@PathVariable String taskId) {
        String deletedId = taskService.findById(taskId).getTaskResult().getId();
        return taskResultService.deleteById(deletedId);
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
