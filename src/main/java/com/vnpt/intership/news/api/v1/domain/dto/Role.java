package com.vnpt.intership.news.api.v1.domain.dto;

import com.vnpt.intership.news.api.v1.common.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private ObjectId id;

    private UserRole roleName;

    private String descriptions;

    private List<User> users = new ArrayList<>();
}
