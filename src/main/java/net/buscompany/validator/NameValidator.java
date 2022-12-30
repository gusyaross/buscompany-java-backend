package net.buscompany.validator;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<Name,String> {

    @Value("${max_name_length}")
    private int maxLengthName;

    @Override
    public boolean isValid(String inputName, ConstraintValidatorContext constraintValidatorContext) {
        if (inputName == null) {
            return true; // отчества может не быть
        }

        return inputName.length() <= maxLengthName && !inputName.matches("^[^А-Яа-я]*$");
    }
}
