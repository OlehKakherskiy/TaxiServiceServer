package ua.kpi.mobiledev.web.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
public class ExceptionController {

    @RequestMapping("/exception/badCredentials")
    public ResponseEntity<ErrorMessage> handleBadCredentialsException(HttpServletRequest request) {
        return createResponseWithCode(request, FORBIDDEN);
    }

    @RequestMapping("/exception/authenticationFailure")
    public ResponseEntity<ErrorMessage> handleAuthenticationException(HttpServletRequest request) {
        return createResponseWithCode(request, UNAUTHORIZED);
    }

    @RequestMapping("/exception/runtimeException")
    public ResponseEntity<ErrorMessage> handleRuntimeException(HttpServletRequest request) {
        return createResponseWithCode(request, INTERNAL_SERVER_ERROR);
    }

    @RequestMapping("/exception/statusCodeException")
    public ResponseEntity<ErrorMessage> handleStatusCodeException(HttpServletRequest request) {
        HttpStatusCodeException exception =
                (HttpStatusCodeException) request.getAttribute("javax.servlet.error.exception");
        return new ResponseEntity<>(new ErrorMessage(exception.getMessage()), exception.getStatusCode());
    }

    private ResponseEntity<ErrorMessage> createResponseWithCode(HttpServletRequest request, HttpStatus status) {
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        return new ResponseEntity<>(new ErrorMessage(exception.getMessage()), status);
    }
}
