package course.spring.jyra.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "boards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    @Id
    private String id;

    private String projectId;
    private String sprintId;

    @Builder.Default
    private List<String> toDoIds = new ArrayList<>();

    @Builder.Default
    private List<String> inProgressIds = new ArrayList<>();

    @Builder.Default
    private List<String> inReviewIds = new ArrayList<>();

    @Builder.Default
    private List<String> doneIds = new ArrayList<>();

    @Builder.Default
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @Builder.Default
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();
}
