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
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "User")
@TypeAlias("User")
@Setter
@Getter
@NoArgsConstructor
public class UserEntity extends BaseEntity {
    @MongoId
    private ObjectId id;

    @Indexed(unique = true, direction = IndexDirection.DESCENDING)
    @NotNull
    private String username;

    @Email
    @Indexed(unique = true, direction = IndexDirection.DESCENDING)
    private String email;

    @NotNull
    private String password;

    private AuthIdentityEntity authIdentity;

    @DBRef(lazy = true)
    private Set<RoleEntity> roles = new HashSet<>();

}
