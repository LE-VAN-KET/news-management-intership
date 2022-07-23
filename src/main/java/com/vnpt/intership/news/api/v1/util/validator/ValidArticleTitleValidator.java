package com.vnpt.intership.news.api.v1.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidArticleTitleValidator implements ConstraintValidator<ValidArticleTitle, String> {
    private static final String ARTICLE_TITLE_PATTERN = "^[a-zA-Z\\d\\s]{1,100}$";
    @Override
    public boolean isValid(String title, ConstraintValidatorContext constraintValidatorContext) {
        Pattern pattern = Pattern.compile(ARTICLE_TITLE_PATTERN);
        Matcher matcher = pattern.matcher(title);
        return matcher.matches();
    }
}
