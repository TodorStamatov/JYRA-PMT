package course.spring.jyra.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
@Data
//@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Administrator extends User {
}
