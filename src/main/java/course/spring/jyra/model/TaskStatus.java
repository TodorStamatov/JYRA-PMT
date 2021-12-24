package course.spring.jyra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus {
    TO_DO("To do"), IN_PROGRESS("In progress"), IN_REVIEW("In review"), DONE("Done");

    private final String readable;
}
