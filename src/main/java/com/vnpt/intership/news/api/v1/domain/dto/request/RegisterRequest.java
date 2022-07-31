package com.vnpt.intership.news.api.v1.domain.dto.request;

import com.vnpt.intership.news.api.v1.util.validator.PasswordMatches;
import com.vnpt.intership.news.api.v1.util.validator.ValidEmail;
import com.vnpt.intership.news.api.v1.util.validator.ValidPassword;
import com.vnpt.intership.news.api.v1.util.validator.ValidUsername;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches
public class RegisterRequest {
    @NotNull
    @NotEmpty
    @ValidUsername
    private String username;

    @NotNull
    @NotEmpty
    @ValidPassword
    private String password;

    private String confirmPassword;

    @ValidEmail
    @NotNull
    @NotEmpty
    private String email;
}
