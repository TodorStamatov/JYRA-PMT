package course.spring.jyra.model;

import course.spring.jyra.service.TaskResultService;
import course.spring.jyra.service.TaskService;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Developer extends User {
    @Builder.Default
    private List<String> assignedTasksIds = new ArrayList<>();

    @Builder.Default
    private List<String> completedTaskResultsIds = new ArrayList<>();

}
