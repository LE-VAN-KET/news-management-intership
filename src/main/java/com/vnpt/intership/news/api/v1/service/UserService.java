package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.dto.User;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.exception.UserNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {


    List<UserEntity> findAll();

    Optional<User> findById(String id);

    void deleteById(String id);

    @Transactional(rollbackFor = {Exception.class, UserNotFoundException.class})
    User updateById(String id, User user);

    User updateUser(User user);
}
