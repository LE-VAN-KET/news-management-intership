package com.vnpt.intership.news.api.v1.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidCategoryKeyValidator implements ConstraintValidator<ValidCategoryKey, String> {
    private static final String CATEGORY_KEY_PATTERN = "^[a-zA-Z0-9]{1,100}$";
    @Override
    public boolean isValid(String categoryKey, ConstraintValidatorContext constraintValidatorContext) {
        Pattern pattern = Pattern.compile(CATEGORY_KEY_PATTERN);
        Matcher matcher = pattern.matcher(categoryKey);
        return matcher.matches();
    }
}
