package assessment.parkinglot.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorResponse {
    private Integer statusCode;
    private Integer errorCode;
    private String message;
}
