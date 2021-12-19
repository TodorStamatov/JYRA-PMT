package course.spring.jyra.dao;

import course.spring.jyra.model.TaskResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskResultRepository extends MongoRepository<TaskResult, String> {
}
