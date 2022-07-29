package com.vnpt.intership.news.api.v1.service.impl;

import com.vnpt.intership.news.api.v1.domain.dto.User;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.domain.mapper.UserMapper;
import com.vnpt.intership.news.api.v1.exception.UserNotFoundException;
import com.vnpt.intership.news.api.v1.repository.UserRepository;
import com.vnpt.intership.news.api.v1.repository.UsersRepository;
import com.vnpt.intership.news.api.v1.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public List<UserEntity> findAll() {

        return userRepository.findAll();
    }


    @Override
    public User findById(String id) {
        UserEntity userEntity = userRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new UserNotFoundException("User was not found.Please checking again!!"));
        User user = new User();
        UserMapper userMapper = new UserMapper();
        user = userMapper.convertToDto(userEntity, user);
        return user;
    }

    @Override
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateUser(User user) {
        UserEntity userEntity = this.usersRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("User was not found.Please checking again!!"));
        User user1 = new User();
        UserEntity userEntity1 = new UserEntity();

        UserMapper userMapper = new UserMapper();
        if (userEntity != null) {
            userEntity.setEmail(user.getEmail());
            userEntity.setUsername(user.getUsername());
        }
        return userEntity == null ? null : userMapper.convertToDto(this.usersRepository.save(userEntity), user1);
    }
}
