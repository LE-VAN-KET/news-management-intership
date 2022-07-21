package com.vnpt.intership.news.api.v1.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.Max;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "Article")
@TypeAlias("Article")
@Getter
@Setter
@NoArgsConstructor
public class ArticleEntity extends BaseEntity{
    @MongoId
    private ObjectId id;

    private String title;

    private String thumbnailUrl;

    @Max(1024)
    private String content;

    @DBRef(lazy = true)
    private UserEntity user;

    @DBRef
    private Set<CategoriesEntity> categories = new HashSet<>();
}
