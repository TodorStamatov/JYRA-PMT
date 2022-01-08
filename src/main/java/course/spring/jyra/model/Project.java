package course.spring.jyra.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @TextIndexed
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
    private String ownerId;

    @NonNull
    @NotNull
    private List<String> developersIds = new ArrayList<>();

    @Builder.Default
    private String currentSprintId = new String();

    @Builder.Default
    private List<String> previousSprintResultsIds = new ArrayList<>();

    @Builder.Default
    private List<String> tasksBacklogIds = new ArrayList<>();

    @TextIndexed
    private String tags;

    @Builder.Default
    private String projectResultId = new String();

    @Builder.Default
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @Builder.Default
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();
}
