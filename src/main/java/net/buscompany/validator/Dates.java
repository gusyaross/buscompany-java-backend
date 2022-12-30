package net.buscompany.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DatesValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dates {
    String message() default "Некорректная запись дат";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
