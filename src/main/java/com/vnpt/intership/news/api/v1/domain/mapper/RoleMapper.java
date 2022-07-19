package com.vnpt.intership.news.api.v1.domain.mapper;

import com.vnpt.intership.news.api.v1.domain.dto.Role;
import com.vnpt.intership.news.api.v1.domain.dto.User;
import com.vnpt.intership.news.api.v1.domain.entity.RoleEntity;
import org.springframework.beans.BeanUtils;

import javax.annotation.PostConstruct;

public class RoleMapper extends BaseMapper<RoleEntity, Role> {
    private UserMapper userMapper;

    @PostConstruct
    public void init() {
        this.userMapper = new UserMapper();
    }
    @Override
    public RoleEntity convertToEntity(Role role, Object... args) {
        RoleEntity roleEntity = new RoleEntity();
        if (role != null) {
            BeanUtils.copyProperties(role, roleEntity, "users");
            roleEntity.setUsers(userMapper.convertToEntitySet(role.getUsers()));
        }
        return roleEntity;
    }

    @Override
    public Role convertToDto(RoleEntity entity, Object... args) {
        Role role = new Role();
        if (entity != null) {
            BeanUtils.copyProperties(entity, role, "users");
            role.setUsers(userMapper.convertToDtoList(entity.getUsers()));
        }
        return role;
    }
}
