package ua.kpi.mobiledev.web.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = InRangeOfConstraintValidator.class)
@Documented
public @interface InRangeOf {
    int requirementId() default 0;

    int elementsCount() default 0;

    String message() default "{inRangeOf.notInRange}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        InRangeOf[] value();
    }
}
