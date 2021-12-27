package course.spring.jyra.service.impl;

import course.spring.jyra.dao.ProjectRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.Project;
import course.spring.jyra.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, MongoTemplate mongoTemplate) {
        this.projectRepository = projectRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project findById(String id) {
        return projectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Project with ID=%s not found.", id)));
    }

    @Override
    public Project findByTitle(String title) {
        return projectRepository.findByTitle(title).orElseThrow(() -> new EntityNotFoundException(String.format("Project with title=%s not found.", title)));
    }

    @Override
    public Project create(Project project) {
        project.setId(null);
        project.setCreated(LocalDateTime.now());
        project.setModified(LocalDateTime.now());
        return projectRepository.insert(project);
    }

    @Override
    public Project deleteById(String id) {
        Project oldProject = findById(id);
        projectRepository.deleteById(id);
        return oldProject;
    }

    @Override
    public Project update(Project project) {
        Project oldProject = findById(project.getId());
        project.setCreated(oldProject.getCreated());
        project.setModified(LocalDateTime.now());
        return projectRepository.save(project);
    }

    @Override
    public List<Project> findBySearch(String searchString) {
        String[] words = searchString.split(" ");
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(words);
        Query query = TextQuery.queryText(criteria);
        return mongoTemplate.find(query, Project.class);
    }

    @Override
    public long count() {
        return projectRepository.count();
    }
}
