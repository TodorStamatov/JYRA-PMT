package course.spring.jyra.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/*
id (generated automatically) - String number;
project - the Project this result is reported for;
endDate - end date for the project;
duration - integer, number of working days for the project;
resultsDescription (optional) - string 10 - 2500 characters String, supporting Markdown syntax;
sprintResults - list of SprintResult for the Sprints completed;
created (generated automatically) - time stamp of the moment the entity was created;
modified (generated automatically) - time stamp of the moment the entity was last modified;
 */
@Document(collection = "projectResults")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ProjectResult {
    @Id
    private String id;

    @NonNull
    @NotNull
    private Project project;

    @NonNull
    @NotNull
    private LocalDateTime endDate=LocalDateTime.now();

    @NonNull
    @NotNull
    private int duration;

    @Size(min = 10, max = 2500, message = "String must be between 10 and 2500 characters String, supporting Markdown syntax.")
    private String resultsDescription;

    @NonNull
    @NotNull
    private List<SprintResult> sprintResultList;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();

}
