package course.spring.jyra.dao;

import course.spring.jyra.model.ProjectResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectResultRepository extends MongoRepository<ProjectResult, Long> {
}
