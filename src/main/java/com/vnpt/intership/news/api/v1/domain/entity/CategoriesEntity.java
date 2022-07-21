package com.vnpt.intership.news.api.v1.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Document(collection = "Categories")
@TypeAlias("Categories")
@Getter
@Setter
@NoArgsConstructor
public class CategoriesEntity {
    @MongoId
    private ObjectId id;

    @NotNull
    private String categoryName;

    @NotNull
    @Indexed(unique = true, direction = IndexDirection.DESCENDING)
    private String categoryKey;

    private String description;

    @DBRef
    private CategoriesEntity parent;

    @DBRef(lazy = true)
    private Set<ArticleEntity> articles;
}
