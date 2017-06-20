package ua.kpi.mobiledev.web.exceptionHandling;

import org.apache.log4j.Logger;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.kpi.mobiledev.exception.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;
import static ua.kpi.mobiledev.exception.ErrorCode.DEFAULT_EXCEPTION_MESSAGE;
import static ua.kpi.mobiledev.exception.ErrorCode.DEFAULT_VALIDATION_EXCEPTION_MESSAGE;

@ControllerAdvice
public class ExceptionHandlingAdvice {

    private static final Logger LOG = Logger.getLogger(ExceptionHandlingAdvice.class.getName());

    private static final String DEFAULT_EXCEPTION_CODE = DEFAULT_EXCEPTION_MESSAGE.name();
    private static final String DEFAULT_VALIDATION_MESSAGE = DEFAULT_VALIDATION_EXCEPTION_MESSAGE.name();

    @Resource(name = "messageSource")
    private ReloadableResourceBundleMessageSource messageSource;

    @Resource(name = "exceptionMessageSource")
    private ReloadableResourceBundleMessageSource exceptionMessageSource;

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<CustomFieldError>> handleRequestValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(processErrors(ex));
    }

    private List<CustomFieldError> processErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toCustomFieldError)
                .collect(Collectors.toList());
    }

    private CustomFieldError toCustomFieldError(FieldError fieldError) {
        Locale locale = getRequestLocale();
        String exceptionMessage;
        try {
            exceptionMessage = messageSource.getMessage(fieldError.getDefaultMessage(), fieldError.getArguments(), locale);
        } catch (NoSuchMessageException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("No message in message source for field with code = '%s'", fieldError.getDefaultMessage()));
            }
            exceptionMessage = messageSource.getMessage(DEFAULT_VALIDATION_MESSAGE, new String[]{fieldError.getField()}, locale);
        }
        return new CustomFieldError(fieldError.getField().trim(), fieldError.getDefaultMessage().trim(), exceptionMessage.trim());
    }

    private Locale getRequestLocale() {
        return LocaleContextHolder.getLocale();
    }

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorMessage> handleSystemException(SystemException ex) {
        LOG.error(ex.getCause());
        return buildResponse(INTERNAL_SERVER_ERROR, ex);
    }

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(RequestException.class)
    public ResponseEntity<ErrorMessage> handleRequestException(RequestException ex) {
        return buildResponse(BAD_REQUEST, ex);
    }

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException ex) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(ex.getCause());
        }
        return buildResponse(NOT_FOUND, ex);
    }

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorMessage> handleForbiddenOperationException(ForbiddenOperationException ex) {
        return buildResponse(FORBIDDEN, ex);
    }

    private ResponseEntity<ErrorMessage> buildResponse(HttpStatus status, AbstractLocalizedException ex) {
        return ResponseEntity.status(status).body(createErrorMessage(ex));
    }

    private ErrorMessage createErrorMessage(AbstractLocalizedException ex) {
        Locale locale = getRequestLocale();
        String errorCode = ex.getErrorCode().name();
        String resultMessage;
        try {
            resultMessage = exceptionMessageSource.getMessage(errorCode, ex.getParams(), locale);
        } catch (NoSuchMessageException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("No message in message source for field with code = '%s'", errorCode));
            }
            resultMessage = exceptionMessageSource.getMessage(DEFAULT_EXCEPTION_CODE, new String[]{errorCode}, locale);
        }
        return new ErrorMessage(resultMessage.trim());
    }
}
