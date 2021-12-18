package course.spring.jyra.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Administrator extends User {
}
