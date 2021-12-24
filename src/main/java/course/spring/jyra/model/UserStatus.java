package course.spring.jyra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    ACTIVE("Active"), CHANGE_PASSWORD("Change password"),
    SUSPENDED("Suspended"), DEACTIVATED("Deactivated");

    private final String readable;
}
