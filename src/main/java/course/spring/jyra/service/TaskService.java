package course.spring.jyra.service;

import course.spring.jyra.model.Task;

import java.util.List;

public interface TaskService {
    List<Task> findAll();

    Task findById(long id);

    Task findByTitle(String title);

    Task create(Task task);

    Task update(Task task);

    Task deleteById(long id);

    long count();
}
