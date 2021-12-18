package course.spring.jyra.dao;

import course.spring.jyra.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, Long> {
}
