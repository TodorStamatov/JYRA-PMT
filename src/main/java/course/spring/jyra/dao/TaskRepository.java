package course.spring.jyra.dao;

import course.spring.jyra.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, Long> {
}
