package course.spring.jyra.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Data
@Document(collection = "sprints")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Slf4j
public class Sprint {
    @Id
    private String id;

    @NonNull
    @NotNull
    @TextIndexed
    @Size(min = 2, max = 120, message = "Sprint title must be between 2 and 120 characters String.")
    private String title;

    @Builder.Default
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate = LocalDateTime.now();

    @Builder.Default
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate = LocalDateTime.now().plusWeeks(2);

    private long duration;

    @NonNull
    @NotNull
    private String projectId;

    @NonNull
    @NotNull
    private String ownerId;

    @Builder.Default
    private List<String> developersIds = new ArrayList<>();

    @Builder.Default
    private List<String> tasksIds = new ArrayList<>();

    @Builder.Default
    private List<String> completedTaskResultsIds = new ArrayList<>();
    private String sprintResultId;

    @Builder.Default
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @Builder.Default
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();

    public void calculateDuration() {
        this.duration = DAYS.between(this.startDate, this.endDate);
        log.info("Calculating sprint duration...");
    }
}
