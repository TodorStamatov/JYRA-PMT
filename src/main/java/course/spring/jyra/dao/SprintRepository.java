package course.spring.jyra.dao;

import course.spring.jyra.model.Sprint;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SprintRepository extends MongoRepository<Sprint, String> {
    Optional<Sprint> findByTitle(String title);
}
