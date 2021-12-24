package course.spring.jyra.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private final int status;
    private final String message;
    private String details;
}
