package com.vnpt.intership.news.api.v1.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private static final String USERNAME_PATTERN = "^(?=.{6,30}$)(?:[a-zA-Z\\d])+$";

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
