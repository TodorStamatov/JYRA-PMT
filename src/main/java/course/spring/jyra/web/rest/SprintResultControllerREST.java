package course.spring.jyra.web.rest;

import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.exception.InvalidClientDataException;
import course.spring.jyra.model.ErrorResponse;
import course.spring.jyra.model.SprintResult;
import course.spring.jyra.service.SprintResultService;
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

    @Autowired
    public SprintResultControllerREST(SprintResultService sprintResultService) {
        this.sprintResultService = sprintResultService;
    }

    @GetMapping("/sprint-results")
    public List<SprintResult> getSprintResults() {
        return sprintResultService.findAll();
    }

    @GetMapping("/{sprintId}/sprint-result")
    public SprintResult getResultsByProjectId(@PathVariable("{sprintId}") String id) {
        return sprintResultService.findBySprintId(id);
    }

    @PostMapping
    public ResponseEntity<SprintResult> addSprint(@RequestBody SprintResult sprintResult) {
        SprintResult created = sprintResultService.create(sprintResult);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .pathSegment("{projectId}").buildAndExpand(created.getSprint().getId()).toUri()).body(created);
    }

    @PutMapping("/{sprintId}/sprint-result")
    public SprintResult updateSprint(@PathVariable("sprintId") String sprintId, @RequestBody SprintResult sprintResult) {
        if (!sprintId.equals(sprintResult.getSprint().getId()))
            throw new InvalidClientDataException(String.format("sprint ID %s from URL doesn't match ID %s in Request body", sprintId, sprintResult.getSprint().getId()));
        return sprintResultService.update(sprintResult);
    }

    @DeleteMapping("{sprintId}/sprint-result")
    public SprintResult deletesprintResult(@PathVariable("sprintId") String id) {
        //TODO: check if the id is for sprint or result
        return sprintResultService.deleteById(id);
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
