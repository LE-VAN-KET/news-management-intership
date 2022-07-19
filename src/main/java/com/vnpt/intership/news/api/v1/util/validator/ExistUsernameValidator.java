package com.vnpt.intership.news.api.v1.util.validator;

import com.vnpt.intership.news.api.v1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistUsernameValidator implements ConstraintValidator<ExistUsername, String> {
    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        // custom annotation validate username when registration
        return false;
    }
}
