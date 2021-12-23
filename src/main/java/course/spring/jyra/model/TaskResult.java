package course.spring.jyra.model;

/*
id (generated automatically) - String number;
task - the Task this result is reported for;
actualEffort - integer number in effort units;
verifiedBy - the ProductOwner" or Developer that has verified the Task successful completion;
resultsDescription (optional) - string 10 - 2500 characters String, supporting Markdown syntax;
created (generated automatically) - time stamp of the moment the entity was created;
modified (generated automatically) - time stamp of the moment the entity was last modified;
 */

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Document(collection = "taskResults")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class TaskResult {
    @Id
    private String id;

    @NotNull
    @NonNull
    private Task task;

    @NotNull
    @NonNull
    private int actualEffort;

    @NotNull
    @NonNull
    private User verifiedBy;

    @Size(min = 10, max = 2500, message = "String must be between 10 and 2500 characters String, supporting Markdown syntax.")
    private String resultsDescription;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();

}
