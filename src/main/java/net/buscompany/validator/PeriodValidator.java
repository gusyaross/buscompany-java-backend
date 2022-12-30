package net.buscompany.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PeriodValidator implements ConstraintValidator<Period, String> {

    @Override
    public boolean isValid(String period, ConstraintValidatorContext context) {
        if (period.equals("daily") || period.equals("odd") || period.equals("even"))
            return true;

        Set<String> daysOfWeek = new HashSet<>(List.of("Sun","Mon","Tue","Wed","Thu","Fri","Sat"));
        Set<String> days = Set.of(period.replaceAll("\\s+","").split(","));

        if (daysOfWeek.containsAll(days)) {
            return true;
        }

        return days.stream().allMatch(str -> Integer.parseInt(str) > 1 && Integer.parseInt(str) <= 31);
    }
}
