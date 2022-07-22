package com.vnpt.intership.news.api.v1.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoExistCategoryKeyValidator.class)
@Documented
public @interface NoExistCategoryKey {
    String message() default "Category Key already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
