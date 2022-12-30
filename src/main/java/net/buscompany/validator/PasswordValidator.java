package net.buscompany.validator;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password,String> {

    @Value("${min_password_length}")
    private int minLengthPassword;

    @Override
    public boolean isValid(String inputPassword, ConstraintValidatorContext constraintValidatorContext) {
        return inputPassword != null && inputPassword.length() >= minLengthPassword;
    }
}
