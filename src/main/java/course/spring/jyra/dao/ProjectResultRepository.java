package course.spring.jyra.dao;

import course.spring.jyra.model.ProjectResult;
import course.spring.jyra.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProjectResultRepository extends MongoRepository<ProjectResult, String> {
}
