package course.spring.jyra.dao;

import course.spring.jyra.model.SprintResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SprintResultRepository extends MongoRepository<SprintResult, Long> {
}
