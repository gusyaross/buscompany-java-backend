package net.buscompany.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ScheduleValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {
    String message() default "Некорректное расписание";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
