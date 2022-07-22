package com.vnpt.intership.news.api.v1.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCategoryKeyValidator.class)
@Documented
public @interface ValidCategoryKey {
    String message() default "Category name allow only contains alphabetic, numeric and over 100 characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
