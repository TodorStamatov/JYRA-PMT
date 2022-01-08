package course.spring.jyra.service.impl;

import course.spring.jyra.dao.SprintRepository;
import course.spring.jyra.dao.TaskRepository;
import course.spring.jyra.dao.TaskResultRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.Sprint;
import course.spring.jyra.model.Task;
import course.spring.jyra.model.TaskResult;
import course.spring.jyra.service.TaskResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskResultServiceImpl implements TaskResultService {
    private final TaskResultRepository taskResultRepository;
    private final TaskRepository taskRepository;
    private final SprintRepository sprintRepository;

    @Autowired
    public TaskResultServiceImpl(TaskResultRepository taskResultRepository, TaskRepository taskRepository, SprintRepository sprintRepository) {
        this.taskResultRepository = taskResultRepository;
        this.taskRepository = taskRepository;
        this.sprintRepository = sprintRepository;
    }

    @Override
    public List<TaskResult> findAll() {
        return taskResultRepository.findAll();
    }

    @Override
    public TaskResult findById(String id) {
        return taskResultRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Task result with ID=%s not found.", id)));
    }

    @Override
    public TaskResult create(TaskResult taskResult) {
        taskResult.setId(null);
        taskResult.setCreated(LocalDateTime.now());
        taskResult.setModified(LocalDateTime.now());
        TaskResult updated = taskResultRepository.insert(taskResult);

        // update task references
        Task task = taskRepository.findById(taskResult.getTaskId()).orElseThrow(() -> new EntityNotFoundException(String.format("Task with ID=%s not found.", taskResult.getTaskId())));
        task.setTaskResultId(updated.getId());
        taskRepository.save(task);

        // if the task belongs to a sprint, update the sprint's reference to task results
        if (task.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(task.getSprintId()).orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found.", task.getSprintId())));
            sprint.getCompletedTaskResultsIds().add(updated.getId());
            sprintRepository.save(sprint);
        }

        return updated;
    }

    @Override
    public TaskResult update(TaskResult taskResult, String oldId) {
        TaskResult oldTaskResult = findById(oldId);

        taskResult.setId(oldTaskResult.getId());
        taskResult.setTaskId(oldTaskResult.getTaskId());
        taskResult.setCreated(oldTaskResult.getCreated());
        taskResult.setModified(LocalDateTime.now());

        return taskResultRepository.save(taskResult);
    }

    @Override
    public TaskResult update(TaskResult taskResult) {
        TaskResult oldTaskResult = findById(taskResult.getId());
        taskResult.setCreated(oldTaskResult.getCreated());
        taskResult.setModified(LocalDateTime.now());
        return taskResultRepository.save(taskResult);
    }

    @Override
    public TaskResult deleteById(String id) {
        TaskResult oldTaskResult = findById(id);
        taskResultRepository.deleteById(id);

        // update task references
        Task task = taskRepository.findById(oldTaskResult.getTaskId()).orElseThrow(() -> new EntityNotFoundException(String.format("Task with ID=%s not found.", oldTaskResult.getTaskId())));
        task.setTaskResultId(null);
        taskRepository.save(task);

        // if the task belongs to a sprint, update the sprint's reference to task results
        if (task.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(task.getSprintId()).orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found.", task.getSprintId())));
            sprint.getCompletedTaskResultsIds().remove(oldTaskResult.getId());
            sprintRepository.save(sprint);
        }

        return oldTaskResult;
    }

    @Override
    public TaskResult findByTaskId(String id) {
        return taskResultRepository.findAll().stream().filter(taskResult -> taskResult.getTaskId().equals(id)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("Task with ID=%s not found.", id)));
    }

    @Override
    public long count() {
        return taskResultRepository.count();
    }
}
