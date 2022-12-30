package net.buscompany.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {
    String message() default "Некорректная запись имени";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
