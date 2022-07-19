package com.vnpt.intership.news.api.v1.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vnpt.intership.news.api.v1.common.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "Role")
@TypeAlias("Role")
@Setter
@Getter
@NoArgsConstructor
public class RoleEntity {
    @MongoId
    private ObjectId id;

    @JsonProperty("roleName")
    @Indexed(unique = true, direction = IndexDirection.DESCENDING)
    private UserRole roleName;

    private String descriptions;

    @DBRef(lazy = true)
    private Set<UserEntity> users = new HashSet<>();
}
