package ua.kpi.mobiledev.web.exceptionHandling;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ErrorMessage {
    private String message;
}
