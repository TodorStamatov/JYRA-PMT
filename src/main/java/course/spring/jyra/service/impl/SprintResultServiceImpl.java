package course.spring.jyra.service.impl;

import course.spring.jyra.dao.SprintResultRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.SprintResult;
import course.spring.jyra.model.TaskResult;
import course.spring.jyra.service.SprintResultService;
import course.spring.jyra.service.TaskResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SprintResultServiceImpl implements SprintResultService {
    private final SprintResultRepository sprintResultRepository;
    private final TaskResultService taskResultService;

    @Autowired
    public SprintResultServiceImpl(SprintResultRepository sprintResultRepository, TaskResultService taskResultService) {
        this.sprintResultRepository = sprintResultRepository;
        this.taskResultService = taskResultService;
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
        return sprintResultRepository.insert(sprintResult);
    }

    @Override
    public SprintResult update(SprintResult sprintResult, String oldId) {
        SprintResult oldSprintResult = findById(oldId);

        sprintResult.setId(oldSprintResult.getId());
        oldSprintResult.getTaskResultsIds().forEach(id -> sprintResult.getTaskResultsIds().add(id));
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
        return oldSprintResult;
    }

    @Override
    public SprintResult findBySprintId(String id) {
        return sprintResultRepository.findAll().stream().filter(sprintResult -> sprintResult.getSprintId().equals(id)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found.", id)));
    }

    private int calculateTeamVelocity(SprintResult sprintResult) {
        return sprintResult.getTaskResultsIds().stream().map(taskResultService::findById).mapToInt(TaskResult::getActualEffort).sum();
    }

    @Override
    public long count() {
        return sprintResultRepository.count();
    }
}
