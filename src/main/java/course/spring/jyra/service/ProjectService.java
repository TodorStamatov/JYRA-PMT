package course.spring.jyra.service;

import course.spring.jyra.model.Project;

import java.util.List;

public interface ProjectService {
    List<Project> findAll();

    Project findById(long id);

    Project findByTitle(String title);

    Project create(Project project);

    Project deleteById(long id);

    Project update(Project project);

    long count();

}
