package course.spring.jyra.service;

import course.spring.jyra.model.ProjectResult;

import java.util.List;

public interface ProjectResultService {
    List<ProjectResult> findAll();

    ProjectResult findById(String id);

    ProjectResult create(ProjectResult projectResult);

    ProjectResult update(ProjectResult projectResult);

    ProjectResult deleteById(String id);

    ProjectResult findByProject(String id);

    long count();
}
