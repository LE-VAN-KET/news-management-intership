package com.vnpt.intership.news.api.v1.controller;

import com.vnpt.intership.news.api.v1.domain.dto.User;
import com.vnpt.intership.news.api.v1.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> list(@PathVariable("id") ObjectId id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @PutMapping("edit/{user}")
    public ResponseEntity<?> editUser(@PathVariable("objectId") String User, @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }

}
