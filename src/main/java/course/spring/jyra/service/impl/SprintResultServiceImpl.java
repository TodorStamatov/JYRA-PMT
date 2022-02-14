package course.spring.jyra.service.impl;

import course.spring.jyra.dao.ProjectRepository;
import course.spring.jyra.dao.SprintRepository;
import course.spring.jyra.dao.SprintResultRepository;
import course.spring.jyra.dao.TaskResultRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.Project;
import course.spring.jyra.model.Sprint;
import course.spring.jyra.model.SprintResult;
import course.spring.jyra.model.TaskResult;
import course.spring.jyra.service.SprintResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SprintResultServiceImpl implements SprintResultService {
    private final SprintResultRepository sprintResultRepository;
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final TaskResultRepository taskResultRepository;

    @Autowired
    public SprintResultServiceImpl(SprintResultRepository sprintResultRepository, SprintRepository sprintRepository, ProjectRepository projectRepository, TaskResultRepository taskResultRepository) {
        this.sprintResultRepository = sprintResultRepository;
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.taskResultRepository = taskResultRepository;
    }

    @Override
    public List<SprintResult> findAll() {
        return sprintResultRepository.findAll();
    }

    @Override
    public SprintResult findById(String id) {
        return sprintResultRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Sprint result with ID=%s not found.", id)));
    }

    @Override
    public SprintResult create(SprintResult sprintResult) {
        sprintResult.setId(null);
        sprintResult.setCreated(LocalDateTime.now());
        sprintResult.setModified(LocalDateTime.now());
        sprintResult.setTeamVelocity(calculateTeamVelocity(sprintResult));

        SprintResult updated = sprintResultRepository.insert(sprintResult);

        // add references to sprint
        Sprint sprint = sprintRepository.findById(sprintResult.getSprintId()).orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found.", sprintResult.getSprintId())));
        sprint.getCompletedTaskResultsIds().forEach(taskResultId -> sprintResult.getTaskResultsIds().add(taskResultId));
        sprint.setSprintResultId(updated.getId());
        sprintRepository.save(sprint);

        // add reference to project
        Project project = projectRepository.findById(sprint.getProjectId()).orElseThrow(() -> new EntityNotFoundException(String.format("Project with ID=%s not found.", sprint.getProjectId())));
        project.getPreviousSprintResultsIds().add(updated.getId());
        project.setCurrentSprintId(null);
        projectRepository.save(project);

        return updated;
    }

    @Override
    public SprintResult update(SprintResult sprintResult, String oldId) {
        SprintResult oldSprintResult = findById(oldId);

        sprintResult.setId(oldSprintResult.getId());
        oldSprintResult.getTaskResultsIds().forEach(id -> sprintResult.getTaskResultsIds().add(id));
        sprintResult.setSprintId(oldSprintResult.getSprintId());
        sprintResult.setCreated(oldSprintResult.getCreated());
        sprintResult.setModified(LocalDateTime.now());
        sprintResult.setTeamVelocity(calculateTeamVelocity(sprintResult));

        return sprintResultRepository.save(sprintResult);
    }

    @Override
    public SprintResult update(SprintResult sprintResult) {
        SprintResult oldSprintResult = findById(sprintResult.getId());
        sprintResult.setCreated(oldSprintResult.getCreated());
        sprintResult.setModified(LocalDateTime.now());
        sprintResult.setTeamVelocity(calculateTeamVelocity(sprintResult));
        return sprintResultRepository.save(sprintResult);
    }

    @Override
    public SprintResult deleteById(String id) {
        SprintResult oldSprintResult = findById(id);
        sprintResultRepository.deleteById(id);

        // delete references from sprint
        Sprint sprint = sprintRepository.findById(oldSprintResult.getSprintId()).orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found.", oldSprintResult.getSprintId())));
        sprint.setSprintResultId(null);
        sprintRepository.save(sprint);

        // delete reference from project
        Project project = projectRepository.findById(sprint.getProjectId()).orElseThrow(() -> new EntityNotFoundException(String.format("Project with ID=%s not found.", sprint.getProjectId())));
        project.getPreviousSprintResultsIds().remove(oldSprintResult.getId());
        projectRepository.save(project);

        return oldSprintResult;
    }

    @Override
    public SprintResult findBySprintId(String id) {
        return sprintResultRepository.findAll().stream().filter(sprintResult -> sprintResult.getSprintId().equals(id)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found.", id)));
    }

    private int calculateTeamVelocity(SprintResult sprintResult) {
        return sprintResult.getTaskResultsIds().stream().map(taskResultId -> taskResultRepository.findById(taskResultId).orElseThrow(() -> new EntityNotFoundException(String.format("Task result with ID=%s not found.", taskResultId)))).mapToInt(TaskResult::getActualEffort).sum();
    }

    @Override
    public long count() {
        return sprintResultRepository.count();
    }
}
