package course.spring.jyra.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "projects")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Project {
    @Id
    private String id;

    @NonNull
    @NotNull
    @Size(min = 2, max = 120, message = "Project title must be between 2 and 120 characters String.")
    private String title;

    @NonNull
    @NotNull
    @Builder.Default
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate = LocalDateTime.now();

    @Size(min = 10, max = 2500, message = "Description must be between 10 and 2500 characters String.")
    private String description;

    @NonNull
    @NotNull
    private ProductOwner owner;

    @NonNull
    @NotNull
    private List<Developer> developers;

    @NonNull
    @NotNull
    private Sprint currentSprint;

    private List<SprintResult> previousSprintResults;
    private List<Task> tasksBacklog;
    private String tags;
    private ProjectResult projectResult;

    @Builder.Default
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @Builder.Default
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();
}
