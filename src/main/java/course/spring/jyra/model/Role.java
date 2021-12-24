package course.spring.jyra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum Role {
    DEVELOPER("Developer"), PRODUCT_OWNER("Product owner"), ADMIN("Admin");

    private final String readable;
}
