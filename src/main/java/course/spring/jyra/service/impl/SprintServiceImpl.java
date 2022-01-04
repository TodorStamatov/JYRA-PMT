package course.spring.jyra.service.impl;

import course.spring.jyra.dao.SprintRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.Sprint;
import course.spring.jyra.model.Task;
import course.spring.jyra.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SprintServiceImpl implements SprintService {
    private final SprintRepository sprintRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public SprintServiceImpl(SprintRepository sprintRepository, MongoTemplate mongoTemplate) {
        this.sprintRepository = sprintRepository;
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
        sprint.setCreated(LocalDateTime.now());
        sprint.setModified(LocalDateTime.now());
        sprint.calculateDuration();
        return sprintRepository.insert(sprint);
    }

    @Override
    public Sprint deleteById(String id) {
        Sprint oldSprint = findById(id);
        sprintRepository.deleteById(id);
        return oldSprint;
    }

    @Override
    public Sprint update(Sprint sprint, String oldId) {
        Sprint oldSprint = findById(oldId);

        sprint.setId(oldSprint.getId());
        sprint.setStartDate(oldSprint.getStartDate());
        oldSprint.getDevelopersIds().forEach(id -> sprint.getDevelopersIds().add(id));
        oldSprint.getTasksIds().forEach(id -> sprint.getTasksIds().add(id));
        oldSprint.getCompletedTaskResultsIds().forEach(id -> sprint.getCompletedTaskResultsIds().add(id));
        sprint.setSprintResultId(oldSprint.getSprintResultId());
        sprint.setCreated(oldSprint.getCreated());
        sprint.setModified(LocalDateTime.now());

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
