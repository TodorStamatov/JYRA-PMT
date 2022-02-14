package course.spring.jyra.service.impl;

import course.spring.jyra.dao.*;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.*;
import course.spring.jyra.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final BoardRepository boardRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, SprintRepository sprintRepository, ProjectRepository projectRepository, BoardRepository boardRepository, MongoTemplate mongoTemplate) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.boardRepository = boardRepository;
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
    public Task create(Task task, String projectId) {
        task.setId(null);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            User user = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new EntityNotFoundException(String.format("User with username=%s could not be found", auth.getName())));
            task.setAddedById(user.getId());
        }

        task.setCreated(LocalDateTime.now());
        task.setModified(LocalDateTime.now());

        Task updated = taskRepository.insert(task);

        for (String devId : task.getDevelopersAssignedIds()) {
            Developer dev = (Developer) userRepository.findById(devId).orElseThrow(() -> new EntityNotFoundException(String.format("User with ID=%s not found.", devId)));
            dev.getAssignedTasksIds().add(task.getId());
            userRepository.save(dev);
        }

        // add sprint and board references
        if (task.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(task.getSprintId()).orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found.", task.getSprintId())));
            sprint.getTasksIds().add(task.getId());
            sprintRepository.save(sprint);

            Board board = boardRepository.findAll().stream().filter(b -> b.getSprintId().equals(task.getSprintId())).findFirst().orElseThrow(() -> new EntityNotFoundException(String.format("Board for sprint with ID=%s not found.", task.getSprintId())));
            switch (task.getStatus()) {
                case TO_DO:
                    board.getToDoIds().add(task.getId());
                    break;
                case IN_PROGRESS:
                    board.getInProgressIds().add(task.getId());
                    break;
                case IN_REVIEW:
                    board.getInReviewIds().add(task.getId());
                    break;
                case DONE:
                    board.getDoneIds().add(task.getId());
                    break;
            }
            boardRepository.save(board);
        }

        // add reference to the project's backlog
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException(String.format("Project with ID=%s not found.", projectId)));
        project.getTasksBacklogIds().add(task.getId());
        projectRepository.save(project);

        return updated;
    }

    @Override
    public Task update(Task task, String oldId, String projectId) {
        Task oldTask = findById(oldId);

        task.setId(oldTask.getId());
        task.setAddedById(oldTask.getAddedById());
        //oldTask.getDevelopersAssignedIds().forEach(id -> task.getDevelopersAssignedIds().add(id));
        task.setTaskResultId(oldTask.getTaskResultId());
        task.setCreated(oldTask.getCreated());
        task.setModified(LocalDateTime.now());

        Collections.sort(oldTask.getDevelopersAssignedIds());
        Collections.sort(task.getDevelopersAssignedIds());

        Task updated = taskRepository.save(task);

        if (oldTask.getDevelopersAssignedIds().equals(task.getDevelopersAssignedIds())) {
            return taskRepository.save(task);
        } else {
            // check if the updated task has additional devs, if so add the task to them
            for (String devId : task.getDevelopersAssignedIds()) {
                Developer dev = (Developer) userRepository.findById(devId).orElseThrow(() -> new EntityNotFoundException(String.format("User with ID=%s not found.", devId)));
                if (!oldTask.getDevelopersAssignedIds().contains(dev.getId())) {
                    dev.getAssignedTasksIds().add(task.getId());
                    userRepository.save(dev);
                }
            }

            // check if the updated task has less devs, if so remove the task from the unassigned devs
            for (String devId : oldTask.getDevelopersAssignedIds()) {
                Developer dev = (Developer) userRepository.findById(devId).orElseThrow(() -> new EntityNotFoundException(String.format("User with ID=%s not found.", devId)));
                if (!task.getDevelopersAssignedIds().contains(dev.getId())) {
                    dev.getAssignedTasksIds().remove(task.getId());
                    userRepository.save(dev);
                }
            }
        }
        // add reference to the sprint
        if (task.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(task.getSprintId()).orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found.", task.getSprintId())));
            sprint.getTasksIds().add(task.getId());
            sprintRepository.save(sprint);

            Board board = boardRepository.findAll().stream().filter(b -> b.getSprintId().equals(task.getSprintId())).findFirst().orElseThrow(() -> new EntityNotFoundException(String.format("Board for sprint with ID=%s not found.", task.getSprintId())));
            if (!task.getStatus().equals(oldTask.getStatus())) {
                switch (oldTask.getStatus()) {
                    case TO_DO:
                        board.getToDoIds().remove(oldTask.getId());
                        break;
                    case IN_PROGRESS:
                        board.getInProgressIds().remove(oldTask.getId());
                        break;
                    case IN_REVIEW:
                        board.getInReviewIds().remove(oldTask.getId());
                        break;
                    case DONE:
                        board.getDoneIds().remove(oldTask.getId());
                        break;
                }
                switch (task.getStatus()) {
                    case TO_DO:
                        board.getToDoIds().add(task.getId());
                        break;
                    case IN_PROGRESS:
                        board.getInProgressIds().add(task.getId());
                        break;
                    case IN_REVIEW:
                        board.getInReviewIds().add(task.getId());
                        break;
                    case DONE:
                        board.getDoneIds().add(task.getId());
                        break;
                }
            }
            boardRepository.save(board);
        }

        // add reference to the project's backlog
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException(String.format("Project with ID=%s not found.", projectId)));
        project.getTasksBacklogIds().add(task.getId());
        projectRepository.save(project);

        return updated;
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
    public Task deleteById(String id, String projectId) {
        Task oldTask = findById(id);
        taskRepository.deleteById(id);

        // delete users' references to the task
        for (String devId : oldTask.getDevelopersAssignedIds()) {
            Developer dev = (Developer) userRepository.findById(devId).orElseThrow(() -> new EntityNotFoundException(String.format("User with ID=%s not found.", devId)));
            dev.getAssignedTasksIds().remove(oldTask.getId());
            userRepository.save(dev);
        }

        // delete the sprint's and board's references to the task
        if (oldTask.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(oldTask.getSprintId()).orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found.", oldTask.getSprintId())));
            sprint.getTasksIds().remove(oldTask.getId());
            sprintRepository.save(sprint);

            Board board = boardRepository.findAll().stream().filter(b -> b.getSprintId().equals(oldTask.getSprintId())).findFirst().orElseThrow(() -> new EntityNotFoundException(String.format("Board for sprint with ID=%s not found.", oldTask.getSprintId())));
            switch (oldTask.getStatus()) {
                case TO_DO:
                    board.getToDoIds().remove(oldTask.getId());
                    break;
                case IN_PROGRESS:
                    board.getInProgressIds().remove(oldTask.getId());
                    break;
                case IN_REVIEW:
                    board.getInReviewIds().remove(oldTask.getId());
                    break;
                case DONE:
                    board.getDoneIds().remove(oldTask.getId());
                    break;
            }
            boardRepository.save(board);
        }

        // delete the project's reference to the task
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException(String.format("Project with ID=%s not found.", projectId)));
        project.getTasksBacklogIds().remove(oldTask.getId());
        projectRepository.save(project);

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
