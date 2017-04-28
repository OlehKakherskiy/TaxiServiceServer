package ua.kpi.mobiledev.web.exceptionHandling;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ua.kpi.mobiledev.exception.ForbiddenOperationException;
import ua.kpi.mobiledev.exception.RequestException;
import ua.kpi.mobiledev.exception.ResourceNotFoundException;
import ua.kpi.mobiledev.exception.SystemException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static ua.kpi.mobiledev.exception.ErrorCode.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath:RESTContext.xml", "classpath:testContext.xml"})
public class ExceptionHandlingAdviceTest {

    private static final String SYSTEM_EXCEPTION = "Exception occurred during registration process. Try again";
    private static final String REQUEST_EXCEPTION = "Пользователь с электронной почтой 'testEmail@gmail.com' уже существует";
    private static final String FORBIDDEN_OPERATION = "Користувач з id=1 не є власником замовлення з id=2";
    private static final String RESOURCE_NOT_FOUND = "Order with id=1 doesn't exist";
    private static final String DEFAULT_MESSAGE = "No message was defined for error code 'TEST_CODE'";

    private static final Locale DEFAULT_LOCALE = new Locale("en", "EN");
    private static final Locale RUSSIAN_LOCALE = new Locale("ru", "RU");
    private static final Locale UKRAINIAN_LOCALE = new Locale("uk", "UA");

    @Resource(name = "exceptionHandlingAdvice")
    private ExceptionHandlingAdvice exceptionHandlingAdvice;

    @Test
    public void shouldReturnResponseWithServerErrorCodeAndMessageInDefaultLocale() {
        setLocale(DEFAULT_LOCALE);
        SystemException thrown = new SystemException(REGISTRATION_GENERAL_SYSTEM_EXCEPTION);
        ResponseEntity<ErrorMessage> expected =
                new ResponseEntity<>(createMessage(SYSTEM_EXCEPTION), INTERNAL_SERVER_ERROR);

        assertEquals(expected, exceptionHandlingAdvice.handleSystemException(thrown));
    }

    @Test
    public void shouldReturnResponseWithErrorCodeAndMessage() {
        RequestException thrown = new RequestException(USER_ALREADY_EXISTS, "testEmail@gmail.com");
        ResponseEntity<ErrorMessage> expected = new ResponseEntity<>(createMessage(REQUEST_EXCEPTION), BAD_REQUEST);
        setLocale(RUSSIAN_LOCALE);

        assertEquals(expected, exceptionHandlingAdvice.handleRequestException(thrown));
    }

    @Test
    public void shouldReturnResponseWithFormattedMessage() {
        ForbiddenOperationException thrown = new ForbiddenOperationException(USER_IS_NOT_ORDER_OWNER, 1, 2);
        ResponseEntity<ErrorMessage> expected
                = new ResponseEntity<>(createMessage(FORBIDDEN_OPERATION), FORBIDDEN);
        setLocale(UKRAINIAN_LOCALE);

        assertEquals(expected, exceptionHandlingAdvice.handleForbiddenOperationException(thrown));
    }

    @Test
    public void shouldReturnResponseWithFormattedMessageWhenMoreMessageParamsAdded() {
        setLocale(DEFAULT_LOCALE);
        ResourceNotFoundException thrown = new ResourceNotFoundException(ORDER_NOT_FOUND_WITH_ID, 1, 2, 3);
        ResponseEntity<ErrorMessage> expected = new ResponseEntity<>(createMessage(RESOURCE_NOT_FOUND), NOT_FOUND);

        assertEquals(expected, exceptionHandlingAdvice.handleResourceNotFoundException(thrown));
    }

    @Test
    public void shouldReturnResponseWithInvalidFieldList() {
        setLocale(DEFAULT_LOCALE);
        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> fieldErrors = asList(
                createFieldError("user.email.invalidFormat", "user.email.invalidFormat"),
                createFieldError("additionalRequirement.idValue.carType", "additionalRequirement.idValue.carType"));
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodArgumentNotValidException thrown = new MethodArgumentNotValidException(null, bindingResult);

        List<CustomFieldError> fieldErrorList = asList(
                new CustomFieldError("fieldName", "user.email.invalidFormat", "Email has incorrect format"),
                new CustomFieldError("fieldName", "additionalRequirement.idValue.carType", "Wrong id value of 'car type' requirement. " +
                        "Valid values : 0 - truck, 1 - passenger car, 2 - minibus")
        );
        ResponseEntity<List<CustomFieldError>> expectedResponse = new ResponseEntity<>(fieldErrorList, BAD_REQUEST);
        assertEquals(expectedResponse, exceptionHandlingAdvice.handleRequestValidationException(thrown));
    }

    private FieldError createFieldError(String code, String defaultMessage) {
        return new FieldError("fieldError", "fieldName", null,
                false, new String[]{code}, new String[]{null}, defaultMessage);
    }

    @Test
    public void shouldReturnDefaultMessageWhenNoMessageIsDefinedForErrorCode() {
        setLocale(DEFAULT_LOCALE);
        ResponseEntity<ErrorMessage> expected = new ResponseEntity<>(createMessage(DEFAULT_MESSAGE), BAD_REQUEST);
        assertEquals(expected, exceptionHandlingAdvice.handleRequestException(new RequestException(TEST_CODE)));
    }

    private void setLocale(Locale locale){
        LocaleContextHolder.setLocaleContext(new SimpleLocaleContext(locale));
    }
    private ErrorMessage createMessage(String message) {
        return new ErrorMessage(message);
    }
}