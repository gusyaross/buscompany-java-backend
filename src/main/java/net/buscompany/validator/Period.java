package net.buscompany.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PeriodValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Period {
    String message() default "Неправильно задан период";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
