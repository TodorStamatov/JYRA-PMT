package course.spring.jyra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus {
    TO_DO("To Do"), IN_PROGRESS("In Progress"), IN_REVIEW("In Review"), DONE("Done");

    private final String readable;
}
