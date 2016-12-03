package ua.kpi.mobiledev.web.exceptionHandling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorControllerAdvice {

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<CustomFieldError>> handleFormValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(processErrors(ex));
    }

    private List<CustomFieldError> processErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toCustomFieldError)
                .collect(Collectors.toList());
    }

    private CustomFieldError toCustomFieldError(FieldError basicFieldError) {
        return new CustomFieldError(basicFieldError.getField(), basicFieldError.getDefaultMessage(),
                messageSource.getMessage(new DefaultMessageSourceResolvable(basicFieldError.getCodes(), basicFieldError.getArguments()), new Locale("en", "EN")));
    }

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getLocalizedMessage()));
    }

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorMessage> handleNPE(NullPointerException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getLocalizedMessage()));
    }

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getLocalizedMessage()));
    }
}
