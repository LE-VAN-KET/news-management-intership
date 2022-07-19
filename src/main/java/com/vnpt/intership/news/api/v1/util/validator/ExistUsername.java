package com.vnpt.intership.news.api.v1.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistUsernameValidator.class)
@Documented
public @interface ExistUsername {
    String message() default "Username already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
