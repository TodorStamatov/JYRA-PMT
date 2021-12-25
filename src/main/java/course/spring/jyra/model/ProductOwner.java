package course.spring.jyra.model;

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
public class ProductOwner extends User {
    private List<String> projectsIds = new ArrayList<>();
    private List<String> completedProjectResultsIds = new ArrayList<>();

    //TODO: fix printProjects
//    public String printProjects() {
//        StringBuilder stringBuilder = new StringBuilder();
//        projects.forEach(project -> stringBuilder.append(String.format("%s, ", project.getTitle())));
//        return stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
//    }
}
