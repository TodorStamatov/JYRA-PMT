package course.spring.jyra.service.impl;

import course.spring.jyra.dao.*;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.*;
import course.spring.jyra.service.SprintService;
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
public class SprintServiceImpl implements SprintService {
    private final SprintRepository sprintRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public SprintServiceImpl(SprintRepository sprintRepository, UserRepository userRepository, ProjectRepository projectRepository, TaskRepository taskRepository, BoardRepository boardRepository, MongoTemplate mongoTemplate) {
        this.sprintRepository = sprintRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.boardRepository = boardRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Sprint> findAll() {
        return sprintRepository.findAll();
    }

    @Override
    public Sprint findById(String id) {
        return sprintRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found.", id)));
    }

    @Override
    public Sprint findByTitle(String title) {
        return sprintRepository.findByTitle(title).orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with title=%s not found.", title)));
    }

    @Override
    public Sprint create(Sprint sprint) {
        sprint.setId(null);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            User user = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new EntityNotFoundException(String.format("User with username=%s could not be found", auth.getName())));
            sprint.setOwnerId(user.getId());
        }

        sprint.setCreated(LocalDateTime.now());
        sprint.setModified(LocalDateTime.now());
        sprint.calculateDuration();
        Sprint updated = sprintRepository.insert(sprint);

        // add reference to project
        Project project = projectRepository.findById(sprint.getProjectId()).orElseThrow(() -> new EntityNotFoundException(String.format("Project with ID=%s not found.", sprint.getProjectId())));
        project.setCurrentSprintId(updated.getId());
        projectRepository.save(project);

        // add references to tasks
        for (String taskId : sprint.getTasksIds()) {
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException(String.format("Task with ID=%s not found.", taskId)));
            task.setSprintId(updated.getId());
            taskRepository.save(task);
        }

        // add reference to board
        Board board = boardRepository.findAll().stream().filter(b -> b.getProjectId().equals(updated.getProjectId())).findFirst().orElseThrow(() -> new EntityNotFoundException(String.format("Board for project with ID=%s not found.", updated.getProjectId())));
        board.setSprintId(updated.getId());
        boardRepository.save(board);

        return updated;
    }

    @Override
    public Sprint update(Sprint sprint, String oldId) {
        Sprint oldSprint = findById(oldId);

        sprint.setId(oldSprint.getId());
        sprint.setStartDate(oldSprint.getStartDate());
        sprint.setSprintResultId(oldSprint.getSprintResultId());
        sprint.setCreated(oldSprint.getCreated());
        sprint.setModified(LocalDateTime.now());

        for (String taskId : sprint.getTasksIds()) {
            // add references to tasks' field sprintId
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException(String.format("Task with ID=%s not found.", taskId)));
            task.setSprintId(sprint.getId());
            taskRepository.save(task);

            // add references to task result for the updated tasks if present
            if (task.getTaskResultId() != null) {
                sprint.getCompletedTaskResultsIds().add(task.getTaskResultId());
            }
        }

        return sprintRepository.save(sprint);
    }

    @Override
    public Sprint update(Sprint sprint) {
        Sprint oldSprint = findById(sprint.getId());
        sprint.setCreated(oldSprint.getCreated());
        sprint.setModified(LocalDateTime.now());
        return sprintRepository.save(sprint);
    }

    @Override
    public Sprint deleteById(String id) {
        Sprint oldSprint = findById(id);
        sprintRepository.deleteById(id);

        // remove reference from project
        Project project = projectRepository.findById(oldSprint.getProjectId()).orElseThrow(() -> new EntityNotFoundException(String.format("Project with ID=%s not found.", oldSprint.getProjectId())));
        project.setCurrentSprintId(null);
        projectRepository.save(project);

        // remove sprintId field from all tasks
        for (String taskId : oldSprint.getTasksIds()) {
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException(String.format("Task with ID=%s not found.", taskId)));
            task.setSprintId(null);
            taskRepository.save(task);
        }
        return oldSprint;
    }

    @Override
    public List<Sprint> findBySearch(String keywords) {
        if (keywords == null || keywords.length() == 0) {
            return sprintRepository.findAll();
        }
        String[] words = keywords.split(" ");
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(words);
        Query query = TextQuery.queryText(criteria);
        return mongoTemplate.find(query, Sprint.class);
    }

    @Override
    public long count() {
        return sprintRepository.count();
    }
}
