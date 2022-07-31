package com.vnpt.intership.news.api.v1.domain.dto.request;

import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import com.vnpt.intership.news.api.v1.util.validator.NoExistCategoryKey;
import com.vnpt.intership.news.api.v1.util.validator.ValidCategoryKey;
import com.vnpt.intership.news.api.v1.util.validator.ValidCategoryName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategory {
    @NotNull
    @NotEmpty(message = "category name is required")
    @ValidCategoryName
    private String categoryName;

    @NotNull
    @NotEmpty(message = "category key not empty")
    @ValidCategoryKey
    @NoExistCategoryKey
    private String categoryKey;

    @NotNull
    @NotEmpty(message = "description is required")
    @Size(min = 1, max = 500, message = "Length must not over 500 characters")
    private String description;

    private CategoriesEntity parent;
}
