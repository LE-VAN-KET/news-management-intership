package com.vnpt.intership.news.api.v1.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId id;

    private String title;

    private String thumbnailUrl;

    @NotNull
    @NotEmpty(message = "content is required")
    @Size(max = 1024)
    private String content;

    private UserEntity user;

    private List<Category> categories = new ArrayList<>();
}
