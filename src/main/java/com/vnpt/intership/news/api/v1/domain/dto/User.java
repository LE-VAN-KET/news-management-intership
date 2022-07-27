package com.vnpt.intership.news.api.v1.domain.dto;

import com.vnpt.intership.news.api.v1.domain.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private ObjectId id;

    private String username;

    private String email;

    private String password;

    private List<Role> roles = new ArrayList<>(); //sao cai nay la list role em
    //trong 1 src hoj dua em em clone code ve a anh
    //tai lieu luc ho dua co bay nhieu a
    //trong nay ho tra ve 1 cai list em doc tai lieu cung k hieu luon
}
