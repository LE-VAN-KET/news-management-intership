package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.dto.User;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import org.bson.types.ObjectId;

import java.util.List;

public interface UserService {


    List<UserEntity> findAll();

    User findById(ObjectId id);

    void deleteById(String id);


    User updateUser(User user);
}
