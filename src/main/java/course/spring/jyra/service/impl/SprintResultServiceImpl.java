package course.spring.jyra.service.impl;

import course.spring.jyra.dao.SprintResultRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.ProjectResult;
import course.spring.jyra.model.Sprint;
import course.spring.jyra.model.SprintResult;
import course.spring.jyra.service.SprintResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SprintResultServiceImpl implements SprintResultService {
    private final SprintResultRepository sprintResultRepository;

    @Autowired
    public SprintResultServiceImpl(SprintResultRepository sprintResultRepository) {
        this.sprintResultRepository = sprintResultRepository;
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
        return sprintResultRepository.insert(sprintResult);
    }

    @Override
    public SprintResult update(SprintResult sprintResult) {
        SprintResult oldSprintResult = findById(sprintResult.getId());
        sprintResult.setCreated(oldSprintResult.getCreated());
        sprintResult.setModified(LocalDateTime.now());
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
        return sprintResultRepository.findAll().stream().filter(sprintResult -> sprintResult.getSprint().getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found or is not finished.", id)));
    }

    @Override
    public long count() {
        return sprintResultRepository.count();
    }
}
