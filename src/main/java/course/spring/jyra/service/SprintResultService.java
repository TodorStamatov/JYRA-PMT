package course.spring.jyra.service;

import course.spring.jyra.model.SprintResult;

import java.util.List;

public interface SprintResultService {
    List<SprintResult> findAll();

    SprintResult findById(long id);

    SprintResult findByProjectTitle(String sprintTitle);

    SprintResult create(SprintResult sprintResult);

    SprintResult update(SprintResult sprintResult);

    SprintResult deleteById(long id);

    long count();
}
