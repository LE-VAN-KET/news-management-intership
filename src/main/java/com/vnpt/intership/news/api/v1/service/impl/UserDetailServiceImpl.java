package com.vnpt.intership.news.api.v1.service.impl;

import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username : " + username));

        Collection<? extends GrantedAuthority> grantedAuthorities = userEntity.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getRoleName().toString()))
                .collect(Collectors.toList());

        return new User(userEntity.getUsername(), userEntity.getPassword(), true, true,
                true, true, grantedAuthorities);
    }
}
