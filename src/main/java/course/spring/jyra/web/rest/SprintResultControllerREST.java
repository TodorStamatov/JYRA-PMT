package course.spring.jyra.web.rest;

import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.exception.InvalidClientDataException;
import course.spring.jyra.model.ErrorResponse;
import course.spring.jyra.model.SprintResult;
import course.spring.jyra.service.SprintResultService;
import course.spring.jyra.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/sprints")
public class SprintResultControllerREST {
    private final SprintResultService sprintResultService;
    private final SprintService sprintService;

    @Autowired
    public SprintResultControllerREST(SprintResultService sprintResultService, SprintService sprintService) {
        this.sprintResultService = sprintResultService;
        this.sprintService = sprintService;
    }

    @GetMapping("/sprint-results")
    public List<SprintResult> getSprintResults() {
        return sprintResultService.findAll();
    }

    @GetMapping("/{sprintId}/sprint-result")
    public SprintResult getResultsByProjectId(@PathVariable String sprintId) {
        return sprintResultService.findBySprintId(sprintId);
    }

    @PostMapping("/{sprintId}/sprint-result")
    public ResponseEntity<SprintResult> addSprintResult(@PathVariable String sprintId, @RequestBody SprintResult sprintResult) {
        if (!sprintId.equals(sprintResult.getSprintId()))
            throw new InvalidClientDataException(String.format("sprint ID %s from URL doesn't match ID %s in Request body", sprintId, sprintResult.getSprintId()));
        SprintResult created = sprintResultService.create(sprintResult);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .pathSegment("{projectId}").buildAndExpand(created.getSprintId()).toUri()).body(created);
    }

    @PutMapping("/{sprintId}/sprint-result")
    public SprintResult updateSprint(@PathVariable String sprintId, @RequestBody SprintResult sprintResult) {
        if (!sprintId.equals(sprintResult.getSprintId()))
            throw new InvalidClientDataException(String.format("sprint ID %s from URL doesn't match ID %s in Request body", sprintId, sprintResult.getSprintId()));
        return sprintResultService.update(sprintResult);
    }

    @DeleteMapping("/{sprintId}/sprint-result")
    public SprintResult deleteSprintResult(@PathVariable String sprintId) {
        String deletedId = sprintService.findById(sprintId).getSprintResultId();
        return sprintResultService.deleteById(deletedId);
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
