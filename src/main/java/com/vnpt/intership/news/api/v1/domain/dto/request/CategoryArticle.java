package com.vnpt.intership.news.api.v1.domain.dto.request;

import com.vnpt.intership.news.api.v1.util.validator.ValidCategoryKey;
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
public class CategoryArticle {
    @NotNull
    @NotEmpty(message = "category key not empty")
    @ValidCategoryKey
    private String categoryKey;
}
