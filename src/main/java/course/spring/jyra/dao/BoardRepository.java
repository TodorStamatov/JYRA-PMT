package course.spring.jyra.dao;

import course.spring.jyra.model.Board;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardRepository extends MongoRepository<Board, Long> {
}
