package com.vnpt.intership.news.api.v1.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import com.vnpt.intership.news.api.v1.util.validator.ValidCategoryKey;
import com.vnpt.intership.news.api.v1.util.validator.ValidCategoryName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId id;

    @NotNull
    @NotEmpty(message = "category name is required")
    @ValidCategoryName
    private String categoryName;

    @NotNull
    @NotEmpty(message = "category key not empty")
    @ValidCategoryKey
    private String categoryKey;

    @NotNull
    @NotEmpty(message = "description is required")
    @Size(min = 1, max = 500, message = "Length must not over 500 characters")
    private String description;

    @NotNull(message =  "category parent not null")
    private CategoriesEntity parent;

    private List<Article> articles = new ArrayList<>();
}
