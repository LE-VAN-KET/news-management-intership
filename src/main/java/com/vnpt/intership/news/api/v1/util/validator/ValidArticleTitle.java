package com.vnpt.intership.news.api.v1.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidArticleTitleValidator.class)
@Documented
public @interface ValidArticleTitle {
    String message() default "Article Title allow only contains alphabetic, numeric and space and over 100 characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
