package course.spring.jyra.service;

import course.spring.jyra.model.TaskResult;

import java.util.List;

public interface TaskResultService {
    List<TaskResult> findAll();

    TaskResult findById(String id);

    TaskResult create(TaskResult taskResult);

    TaskResult update(TaskResult taskResult);

    TaskResult deleteById(String id);

    long count();
}
