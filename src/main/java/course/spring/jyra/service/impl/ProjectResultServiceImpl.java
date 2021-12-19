package course.spring.jyra.service.impl;

import course.spring.jyra.dao.ProjectResultRepository;
import course.spring.jyra.exception.EntityNotFoundException;
import course.spring.jyra.model.ProjectResult;
import course.spring.jyra.model.Sprint;
import course.spring.jyra.service.ProjectResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectResultServiceImpl implements ProjectResultService {
    private final ProjectResultRepository projectResultRepository;

    @Autowired
    public ProjectResultServiceImpl(ProjectResultRepository projectResultRepository) {
        this.projectResultRepository = projectResultRepository;
    }

    @Override
    public List<ProjectResult> findAll() {
        return projectResultRepository.findAll();
    }

    @Override
    public ProjectResult findById(String id) {
        return projectResultRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Project result with ID=%s not found.", id)));
    }

    @Override
    public ProjectResult create(ProjectResult projectResult) {
        projectResult.setId(null);
        projectResult.setCreated(LocalDateTime.now());
        projectResult.setModified(LocalDateTime.now());
        return projectResultRepository.insert(projectResult);
    }

    @Override
    public ProjectResult update(ProjectResult projectResult) {
        ProjectResult oldProjectResult = findById(projectResult.getId());
        projectResult.setCreated(oldProjectResult.getCreated());
        projectResult.setModified(LocalDateTime.now());
        return projectResultRepository.save(projectResult);
    }

    @Override
    public ProjectResult deleteById(String id) {
        ProjectResult oldProjectResult = findById(id);
        projectResultRepository.deleteById(id);
        return oldProjectResult;
    }

    @Override
    public long count() {
        return projectResultRepository.count();
    }
}
