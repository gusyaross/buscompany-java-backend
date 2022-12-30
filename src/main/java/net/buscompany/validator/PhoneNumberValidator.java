package net.buscompany.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String inputNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (inputNumber == null || inputNumber.equals("")){
            return false;
        }

        String number = inputNumber.replaceAll("-", "");

        if(number.startsWith("+")){
            number = number.substring(1);
        }

        if(number.matches("[^0-9]")){
            return false;
        }

        if((number.charAt(0) != '7' && number.charAt(0) != '8') || number.length() != 11) {;
            return false;
        }

        return true;
    }
}
