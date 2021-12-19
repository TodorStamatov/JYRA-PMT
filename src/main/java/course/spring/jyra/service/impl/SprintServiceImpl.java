package course.spring.jyra.service.impl;

import course.spring.jyra.dao.SprintRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.Project;
import course.spring.jyra.model.Sprint;
import course.spring.jyra.model.Sprint;
import course.spring.jyra.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SprintServiceImpl implements SprintService {
    private final SprintRepository sprintRepository;

    @Autowired
    public SprintServiceImpl(SprintRepository sprintRepository) {
        this.sprintRepository = sprintRepository;
    }

    @Override
    public List<Sprint> findAll() {
        return sprintRepository.findAll();
    }

    @Override
    public Sprint findById(long id) {
        return sprintRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Sprint with ID=%s not found.", id)));
    }

    @Override
    public Sprint create(Sprint sprint) {
        sprint.setId(null);
        sprint.setCreated(LocalDateTime.now());
        sprint.setModified(LocalDateTime.now());
        return sprintRepository.insert(sprint);
    }

    @Override
    public Sprint deleteById(long id) {
        Sprint oldSprint = findById(id);
        sprintRepository.deleteById(id);
        return oldSprint;
    }

    @Override
    public Sprint update(Sprint sprint) {
        Sprint oldSprint = findById(sprint.getId());
        sprint.setCreated(oldSprint.getCreated());
        sprint.setModified(LocalDateTime.now());
        return sprintRepository.save(sprint);
    }

    @Override
    public long count() {
        return sprintRepository.count();
    }
}
