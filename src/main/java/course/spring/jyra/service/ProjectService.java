package course.spring.jyra.service;

import course.spring.jyra.model.Project;

import java.util.List;

public interface ProjectService {
    List<Project> findAll();

    Project findById(String id);

    Project findByTitle(String title);

    Project create(Project project);

    Project deleteById(String id);

    Project update(Project project);

    long count();

}
