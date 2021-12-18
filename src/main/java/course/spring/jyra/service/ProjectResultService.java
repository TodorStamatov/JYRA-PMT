package course.spring.jyra.service;

import course.spring.jyra.model.ProjectResult;

import java.util.List;

public interface ProjectResultService {
    List<ProjectResult> findAll();

    ProjectResult findById(long id);

    ProjectResult findByProjectTitle(String projectTitle);

    ProjectResult create(ProjectResult projectResult);

    ProjectResult update(ProjectResult projectResult);

    ProjectResult deleteById(long id);

    long count();
}
