package com.vnpt.intership.news.api.v1.repository;

import com.vnpt.intership.news.api.v1.domain.dto.User;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepository extends MongoRepository<UserEntity, ObjectId> {
}
