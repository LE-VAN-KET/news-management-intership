package com.vnpt.intership.news.api.v1.util.validator;

import com.vnpt.intership.news.api.v1.domain.dto.request.RegisterRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        RegisterRequest registerRequest = (RegisterRequest) o;
        return registerRequest.getPassword().equals(registerRequest.getMatchingPassword());
    }
}
