package com.vnpt.intership.news.api.v1.util.validator;

import com.vnpt.intership.news.api.v1.domain.dto.request.RegisterRequest;
import com.vnpt.intership.news.api.v1.domain.dto.request.ResetPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o instanceof RegisterRequest) {
            RegisterRequest registerRequest = (RegisterRequest) o;
            String hashPass = passwordEncoder.encode(registerRequest.getPassword());
//            return registerRequest.getPassword().equals(registerRequest.getConfirmPassword());
            return passwordEncoder.matches(registerRequest.getConfirmPassword(), hashPass);
        } else if (o instanceof ResetPasswordRequest) {
            ResetPasswordRequest resetPasswordRequest = (ResetPasswordRequest) o;
            String hashPass = passwordEncoder.encode(resetPasswordRequest.getPassword());
//            return resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword());
            return passwordEncoder.matches(resetPasswordRequest.getConfirmPassword(), hashPass);
        }

        return true;
    }
}
