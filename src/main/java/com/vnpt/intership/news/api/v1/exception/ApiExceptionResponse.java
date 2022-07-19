package com.vnpt.intership.news.api.v1.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vnpt.intership.news.api.v1.common.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiExceptionResponse {
    private ErrorCode code;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private List<ApiError> errors;

    public void addValidationError(ApiError build) {
        if(Objects.isNull(errors)){
            errors = new ArrayList<>();
        }
        errors.add(build);
    }
}
