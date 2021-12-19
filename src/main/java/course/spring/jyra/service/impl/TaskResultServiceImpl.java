package course.spring.jyra.service.impl;

import course.spring.jyra.dao.TaskResultRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.SprintResult;
import course.spring.jyra.model.TaskResult;
import course.spring.jyra.service.TaskResultService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class TaskResultServiceImpl implements TaskResultService {
    private final TaskResultRepository taskResultRepository;

    @Autowired
    public TaskResultServiceImpl(TaskResultRepository taskResultRepository) {
        this.taskResultRepository = taskResultRepository;
    }

    @Override
    public List<TaskResult> findAll() {
        return taskResultRepository.findAll();
    }

    @Override
    public TaskResult findById(long id) {
        return taskResultRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Task result with ID=%s not found.", id)));
    }

    @Override
    public TaskResult create(TaskResult taskResult) {
        taskResult.setId(null);
        taskResult.setCreated(LocalDateTime.now());
        taskResult.setModified(LocalDateTime.now());
        return taskResultRepository.insert(taskResult);
    }

    @Override
    public TaskResult update(TaskResult taskResult) {
        return null;
    }

    @Override
    public TaskResult deleteById(long id) {
        TaskResult oldTaskResult=findById(id);
        taskResultRepository.deleteById(id);
        return oldTaskResult;
    }

    @Override
    public long count() {
        return taskResultRepository.count();
    }
}
