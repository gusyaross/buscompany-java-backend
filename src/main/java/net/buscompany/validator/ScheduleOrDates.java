package net.buscompany.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ScheduleOrDatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduleOrDates {
    String message() default "Должно быть заполнено или расписание, или даты, но не вместе";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
