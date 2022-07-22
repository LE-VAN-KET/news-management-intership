package com.vnpt.intership.news.api.v1.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidCategoryNameValidator implements ConstraintValidator<ValidCategoryName, String> {
    private static final String CATEGORY_NAME_PATTERN = "^[a-zA-Z0-9\\s]{1,100}$";
    @Override
    public boolean isValid(String categoryName, ConstraintValidatorContext constraintValidatorContext) {
        Pattern pattern = Pattern.compile(CATEGORY_NAME_PATTERN);
        Matcher matcher = pattern.matcher(categoryName);
        return matcher.matches();
    }
}
