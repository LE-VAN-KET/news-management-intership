package com.vnpt.intership.news.api.v1.controller;

import com.vnpt.intership.news.api.v1.domain.dto.User;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping
    public String list(){
        List<UserEntity> list = userService.findAll();
        return ("admin/accounts");
    }

    @PutMapping("edit/{user}")
    public ResponseEntity<?> editUser(@PathVariable("objectId") String User,
                                      @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }


}
