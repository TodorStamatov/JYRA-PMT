package course.spring.jyra.service;

import course.spring.jyra.model.SprintResult;

import java.util.List;

public interface SprintResultService {
    List<SprintResult> findAll();

    SprintResult findById(String id);

    SprintResult create(SprintResult sprintResult);

    SprintResult update(SprintResult sprintResult, String oldId);

    SprintResult update(SprintResult sprintResult);

    SprintResult deleteById(String id);

    SprintResult findBySprintId(String id);

    long count();
}
