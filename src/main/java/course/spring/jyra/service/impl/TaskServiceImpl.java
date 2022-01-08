package course.spring.jyra.service.impl;

import course.spring.jyra.dao.SprintRepository;
import course.spring.jyra.dao.TaskRepository;
import course.spring.jyra.dao.UserRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.Task;
import course.spring.jyra.model.User;
import course.spring.jyra.service.TaskService;
import course.spring.jyra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SprintRepository sprintRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, SprintRepository sprintRepository, MongoTemplate mongoTemplate) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.sprintRepository = sprintRepository;
        this.mongoTemplate = mongoTemplate;
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            User user = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new EntityNotFoundException(String.format("User with username=%s could not be found", auth.getName())));
            task.setAddedById(user.getId());
        }

        task.setCreated(LocalDateTime.now());
        task.setModified(LocalDateTime.now());
        return taskRepository.insert(task);
    }

    @Override
    public Task update(Task task, String oldId) {
        Task oldTask = findById(oldId);

        task.setId(oldTask.getId());
        task.setAddedById(oldTask.getAddedById());
        oldTask.getDevelopersAssignedIds().forEach(id -> task.getDevelopersAssignedIds().add(id));
        task.setTaskResultId(oldTask.getTaskResultId());
        task.setCreated(oldTask.getCreated());
        task.setModified(LocalDateTime.now());

        return taskRepository.save(task);
    }

    @Override
    public Task update(Task task) {
        Task oldTask = findById(task.getId());
        task.setModified(LocalDateTime.now());
        task.setAddedById(oldTask.getAddedById());
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
    public List<Task> findBySearch(String keywords) {
        if (keywords == null || keywords.length() == 0) {
            return taskRepository.findAll();
        }
        String[] words = keywords.split(" ");
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(words);
        Query query = TextQuery.queryText(criteria);
        return mongoTemplate.find(query, Task.class);
    }

    @Override
    public long count() {
        return taskRepository.count();
    }
}
