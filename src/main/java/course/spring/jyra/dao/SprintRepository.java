package course.spring.jyra.dao;

import course.spring.jyra.model.Sprint;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SprintRepository extends MongoRepository<Sprint, String> {
}
