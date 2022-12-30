package net.buscompany.validator;

import net.buscompany.dto.request.admin.AddTripDtoRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ScheduleOrDatesValidator implements ConstraintValidator<ScheduleOrDates, AddTripDtoRequest> {

    @Override
    public boolean isValid(AddTripDtoRequest addTripDtoRequest, ConstraintValidatorContext context) {
        if (addTripDtoRequest == null)
            return false;

        if (addTripDtoRequest.getScheduleDtoRequest() == null && addTripDtoRequest.getDates() == null)
            return false;

        return addTripDtoRequest.getScheduleDtoRequest() == null || addTripDtoRequest.getDates() == null;
    }
}
