package com.vnpt.intership.news.api.v1.domain.mapper;

import com.vnpt.intership.news.api.v1.domain.dto.User;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import org.springframework.beans.BeanUtils;

import javax.annotation.PostConstruct;

public class UserMapper extends BaseMapper<UserEntity, User> {

    private RoleMapper roleMapper;

    @PostConstruct
    public void init() {
        this.roleMapper = new RoleMapper();
    }
    @Override
    public UserEntity convertToEntity(User user, Object... args) {
        UserEntity userEntity = new UserEntity();
        if (user != null) {
            BeanUtils.copyProperties(user, userEntity, "roles");
            userEntity.setRoles(roleMapper.convertToEntitySet(user.getRoles()));
        }
        return userEntity;
    }

    @Override
    public User convertToDto(UserEntity entity, Object... args) {
        User user = new User();
        if (entity != null) {
            BeanUtils.copyProperties(entity, user, "roles");
            user.setRoles(roleMapper.convertToDtoList(entity.getRoles()));
        }
        return user;
    }
}
