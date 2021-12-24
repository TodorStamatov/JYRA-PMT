package course.spring.jyra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskType {
    TASK("Task"), SUBTASK("Subtask"), BUG("Bug"), STORY("Story"),
    UI("User Interface (UI)"), QA("Quality Assurance (QA)"), DOCS("Documentation"), EPIC("Epic"),
    FEATURE("Feature"), IMPROVEMENT("Improvement"), SPIKE("Spike"), OTHER("Other");

    private final String readable;
}
//Mapping to old:
//Research => Spike
//Design => UI
//Bug fixing => BUG
//Documentation => Docs