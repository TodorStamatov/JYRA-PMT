package course.spring.jyra.service;

import course.spring.jyra.model.Task;

import java.util.List;

public interface TaskService {
    List<Task> findAll();

    Task findById(String id);

    Task findByTitle(String title);

    Task create(Task task);

    Task create(Task task, String projectId);

    Task update(Task task, String oldId, String projectId);

    Task update(Task task);

    Task deleteById(String id);

    Task deleteById(String id, String projectId);

    List<Task> findBySearch(String keywords);

    long count();
}
