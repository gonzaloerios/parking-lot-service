package assessment.parkinglot.exception;

import assessment.parkinglot.controller.response.ErrorResponse;
import assessment.parkinglot.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PklExceptionHandler {
    @ExceptionHandler(PklNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(PklNotFoundException ex) {
        ErrorCode error= ex.getError();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), error.getCode(), error.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(PklBadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(PklBadRequestException ex) {
        ErrorCode error= ex.getError();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), error.getCode(), error.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(PklErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalErrorException(PklErrorException ex) {
        ErrorCode error= ex.getError();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getCode(), error.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownErrorException(Exception ex) {
        ErrorCode error= ErrorCode.UNKNOWN_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getCode(), error.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
