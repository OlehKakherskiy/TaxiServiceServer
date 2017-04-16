package ua.kpi.mobiledev.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD,TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FutureTimeConstraintValidator.class)
@Documented
public @interface FutureTime {
    String message() default "{temporal.notFuture}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
