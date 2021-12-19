package course.spring.jyra.dao;

import course.spring.jyra.model.Project;
import course.spring.jyra.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProjectRepository extends MongoRepository<Project, Long> {
    Optional<Project> findByTitle(String title);
}
