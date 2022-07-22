package com.vnpt.intership.news.api.v1.util.validator;

import com.vnpt.intership.news.api.v1.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoExistCategoryKeyValidator implements ConstraintValidator<NoExistCategoryKey, String> {
    @Autowired
    private CategoriesService categoriesService;

    @Override
    public boolean isValid(String categoryKey, ConstraintValidatorContext constraintValidatorContext) {
        return !categoriesService.existCategoryByCategoryKey(categoryKey);
    }
}
