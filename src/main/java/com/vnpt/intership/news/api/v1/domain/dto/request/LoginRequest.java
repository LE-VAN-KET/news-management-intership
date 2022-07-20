package com.vnpt.intership.news.api.v1.domain.dto.request;

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
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotNull
    @NotEmpty(message = "Username is required")
    @ValidUsername
    private String username;

    @NotNull
    @NotEmpty(message = "Password is required")
    @ValidPassword
    private String password;
}
