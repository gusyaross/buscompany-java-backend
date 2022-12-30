package net.buscompany.validator;

import net.buscompany.dto.request.admin.ScheduleDtoRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ScheduleValidator implements ConstraintValidator<Schedule, ScheduleDtoRequest> {

    @Override
    public boolean isValid(ScheduleDtoRequest scheduleDto, ConstraintValidatorContext context) {
        if (scheduleDto == null)
            return true;

        if (scheduleDto.getFromDate() == null || scheduleDto.getToDate() == null)
            return false;

        return scheduleDto.getFromDate().isBefore(scheduleDto.getToDate());
    }
}
