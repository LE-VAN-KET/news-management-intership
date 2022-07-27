package com.vnpt.intership.news.api.v1.service.impl;

import com.vnpt.intership.news.api.v1.domain.dto.User;
import com.vnpt.intership.news.api.v1.domain.entity.RoleEntity;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.domain.mapper.RoleMapper;
import com.vnpt.intership.news.api.v1.domain.mapper.UserMapper;
import com.vnpt.intership.news.api.v1.exception.UserNotFoundException;
import com.vnpt.intership.news.api.v1.repository.UserRepository;
import com.vnpt.intership.news.api.v1.repository.UsersRepository;
import com.vnpt.intership.news.api.v1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public List<UserEntity> findAll(){
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(String id){
        return userRepository.findById(id);
    }

    @Override
    public void deleteById(String id){
        userRepository.deleteById(id);
    }

    @Override
    @Transactional  (rollbackFor = {Exception.class, UserNotFoundException.class})
    public User updateById(String id, User user) {
//        // check category id exist
//        User userEntity = userRepository.findById(id)
//                .orElseThrow(() -> new UserNotFoundException("User not found with id:" + id));
//        String categoryKeyParent = category.getParent().getCategoryKey();
//        if (categoryKeyParent != null && !categoryKeyParent.equals(categoriesEntity.getParent().getCategoryKey())) {
//            UserEntity parent = categoriesRepository.findByCategoryKey(categoryKeyParent)
//                    .orElseThrow(() -> new CategoryException("Category Key " + categoryKeyParent + " not found with"));
//            categoriesEntity.setParent(parent);
//        }
//
//        // update 3 field category
//        categoriesEntity.setCategoryKey(category.getCategoryKey());
//        categoriesEntity.setCategoryName(category.getCategoryName());
//        categoriesEntity.setDescription(category.getDescription());
//
//        return categoriesMapper.convertToDto(categoriesRepository.save(categoriesEntity));
        return null;
    }

    @Override
    public User updateUser(User user){
        UserEntity userEntity = this.usersRepository.findById(user.getId()).orElse(null);
        User user1 = new User();
        UserEntity userEntity1 = new UserEntity();
            RoleMapper roleMapper = new RoleMapper();
            UserMapper userMapper = new UserMapper();
        if(userEntity != null){
            Set<RoleEntity> set = new HashSet<>();
            RoleEntity roleEntity = new RoleEntity();
            RoleEntity role = (RoleEntity) roleMapper.convertToEntity(user.getRoles(), roleEntity);
            set.add(role);
            userEntity.setEmail(user.getEmail());

            userEntity.setRoles(set); //update user thi lay id cua han thoi pk ah dung roi e
            userEntity.setUsername(user.getUsername());
            userEntity.setPassword(user.getPassword());
        }/// roi do e
        return userEntity == null ? null : userMapper.convertToDto(this.usersRepository.save(userEntity), user1);
    }
}
