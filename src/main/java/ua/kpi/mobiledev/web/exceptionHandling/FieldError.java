package ua.kpi.mobiledev.web.exceptionHandling;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.ConstraintViolation;

@Data
@AllArgsConstructor
public class FieldError {

    public static FieldError of(ConstraintViolation<?> ex) {
        String field = ex.getPropertyPath().toString();
        field = field.substring(field.indexOf('.'));
        return new FieldError(field, ex.getMessageTemplate(), ex.getMessage());
    }

    private String field;

    private String code;

    private String message;
}
