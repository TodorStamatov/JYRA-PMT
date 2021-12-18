package course.spring.jyra.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Developer extends User {
    private List<Task> assignedTask;
    private List<TaskResult> completedTaskResults;
}
