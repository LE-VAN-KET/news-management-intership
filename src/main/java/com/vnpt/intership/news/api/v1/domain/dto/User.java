package com.vnpt.intership.news.api.v1.domain.dto;

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
public class User {
    private ObjectId id;

    private String username;

    private String email;

    private String password;

    private List<Role> roles = new ArrayList<>();
}
