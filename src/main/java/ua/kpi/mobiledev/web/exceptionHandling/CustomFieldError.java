package ua.kpi.mobiledev.web.exceptionHandling;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.ConstraintViolation;

@Data
@AllArgsConstructor
public class CustomFieldError {

    private String field;

    private String code;

    private String message;
}
