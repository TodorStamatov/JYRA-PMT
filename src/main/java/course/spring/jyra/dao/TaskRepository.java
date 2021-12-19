package course.spring.jyra.dao;

import course.spring.jyra.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {
    Optional<Task> findByTitle(String title);
}
