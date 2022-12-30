package net.buscompany.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.List;

public class DatesValidator implements ConstraintValidator<Dates, List<LocalDate>> {

    @Override
    public boolean isValid(List<LocalDate> dates, ConstraintValidatorContext context) {
        if (dates == null)
            return true;

        LocalDate current = LocalDate.now(context.getClockProvider().getClock());

        for (LocalDate date : dates)
            if (date == null || date.isBefore(current))
                return false;

        return true;
    }
}
