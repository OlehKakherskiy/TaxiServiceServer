package ua.kpi.mobiledev.web.exceptionHandling;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorControllerAdvice {

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<FieldError>> handleFormValidationException(ConstraintViolationException ex) {
        return ResponseEntity.badRequest()
                .body(ex.getConstraintViolations()
                        .stream()
                        .map(FieldError::of)
                        .collect(Collectors.toList()));
    }

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getLocalizedMessage()));
    }
}
