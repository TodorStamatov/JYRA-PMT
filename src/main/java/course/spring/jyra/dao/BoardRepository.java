package course.spring.jyra.dao;

import course.spring.jyra.model.Board;
import course.spring.jyra.model.Sprint;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BoardRepository extends MongoRepository<Board, String> {
}
