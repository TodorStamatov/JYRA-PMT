package course.spring.jyra.service.impl;

import course.spring.jyra.dao.ProjectRepository;
import course.spring.jyra.dao.UserRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.Developer;
import course.spring.jyra.model.ProductOwner;
import course.spring.jyra.model.Project;
import course.spring.jyra.model.User;
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
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, MongoTemplate mongoTemplate) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
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

        Project updated = projectRepository.insert(project);

//        update cross-references
        ProductOwner productOwner = (ProductOwner) userRepository.findById(project.getOwnerId()).orElseThrow(() -> new EntityNotFoundException(String.format("User with ID=%s not found", project.getOwnerId())));
        productOwner.getProjectsIds().add(project.getId());
        userRepository.save(productOwner);

        return updated;
    }

    @Override
    public Project update(Project project) {
        Project oldProject = findById(project.getId());
        project.setCreated(oldProject.getCreated());
        project.setModified(LocalDateTime.now());
        return projectRepository.save(project);
    }

    @Override
    public Project update(Project project, String oldId) {
        Project oldProject = findById(oldId);

//        Copy fields that are not set in the form
        project.setId(oldProject.getId());
        project.setStartDate(oldProject.getStartDate());
        project.setCurrentSprintId(oldProject.getCurrentSprintId());
        oldProject.getPreviousSprintResultsIds().forEach(id -> project.getPreviousSprintResultsIds().add(id));
        oldProject.getTasksBacklogIds().forEach(id -> project.getTasksBacklogIds().add(id));
        project.setProjectResultId(oldProject.getProjectResultId());
        project.setCreated(oldProject.getCreated());
        project.setModified(LocalDateTime.now());

        if (!oldProject.getOwnerId().equals(project.getOwnerId())) {
            Project updated = projectRepository.save(project);

//        update cross-references (NOT TESTED)
            ProductOwner productOwner = (ProductOwner) userRepository.findById(project.getOwnerId()).orElseThrow(() -> new EntityNotFoundException(String.format("User with ID=%s not found", project.getOwnerId())));
            productOwner.getProjectsIds().add(project.getId());
            userRepository.save(productOwner);

            return updated;
        } else {
            return projectRepository.save(project);
        }
    }

    @Override
    public List<Project> findBySearch(String keywords) {
        if (keywords == null || keywords.length() == 0) {
            return projectRepository.findAll();
        }
        String[] words = keywords.split(" ");
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(words);
        Query query = TextQuery.queryText(criteria);
        return mongoTemplate.find(query, Project.class);
    }

    @Override
    public Project deleteById(String id) {
        Project oldProject = findById(id);
        projectRepository.deleteById(id);

        ProductOwner po = (ProductOwner) userRepository.findById(oldProject.getOwnerId()).orElseThrow(() -> new EntityNotFoundException(String.format("User with ID=%s not found.", oldProject.getOwnerId())));
        po.getProjectsIds().remove(oldProject.getId());
        userRepository.save(po);

        return oldProject;
    }

    @Override
    public long count() {
        return projectRepository.count();
    }
}
