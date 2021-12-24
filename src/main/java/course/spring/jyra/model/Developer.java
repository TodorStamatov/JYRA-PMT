package course.spring.jyra.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Developer extends User {
    private List<Task> assignedTasks;
    private List<TaskResult> completedTaskResults;

    public String printAssignedTasks() {
        StringBuilder stringBuilder = new StringBuilder();
        assignedTasks.forEach(task -> stringBuilder.append(String.format("%s, ", task.getTitle())));
        return stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
    }
}
