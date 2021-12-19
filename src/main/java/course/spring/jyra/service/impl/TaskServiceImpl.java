package course.spring.jyra.service.impl;

import course.spring.jyra.dao.SprintRepository;
import course.spring.jyra.dao.TaskRepository;
import course.spring.jyra.dao.UserRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.exception.InvalidEntityException;
import course.spring.jyra.model.Developer;
import course.spring.jyra.model.Task;
import course.spring.jyra.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SprintRepository sprintRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, SprintRepository sprintRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.sprintRepository = sprintRepository;
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task findById(String id) {
        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Task with ID=%s not found.", id)));
    }

    @Override
    public Task findByTitle(String title) {
        return taskRepository.findByTitle(title).orElseThrow(() -> new EntityNotFoundException(String.format("Task with title=%s not found.", title)));
    }

    @Override
    public Task create(Task task) {
        task.setId(null);
        task.setCreated(LocalDateTime.now());
        task.setModified(LocalDateTime.now());
        return taskRepository.insert(task);
    }

    @Override
    public Task update(Task task) {
        Task oldTask = findById(task.getId());
        task.setModified(LocalDateTime.now());
        task.setAddedBy(oldTask.getAddedBy());
//        task.setSprint(sprintRepository.findById(oldTask.getSprint().getId()).orElseThrow(() -> new InvalidEntityException("Sprint of task could not ne changed")));
//        task.setAddedBy(userRepository.findById(oldTask.getAddedBy().getId()).orElseThrow(() -> new InvalidEntityException("Author of task could not ne changed")));
//        task.getDevelopersAssigned().clear();
//
//        oldTask.getDevelopersAssigned()
//                .forEach(developer -> task.getDevelopersAssigned()
//                        .add((Developer) userRepository.findById(
//                                        developer.getId())
//                                .orElseThrow(
//                                        () -> new EntityNotFoundException(
//                                                String.format("Developer with ID=%d could not be found", developer.getId())
//                                        )
//                                )
//                        )
//                );
        return taskRepository.save(task);
    }

    @Override
    public Task deleteById(String id) {
        Task oldTask = findById(id);
        taskRepository.deleteById(id);
        return oldTask;
    }

    @Override
    public long count() {
        return taskRepository.count();
    }
}
