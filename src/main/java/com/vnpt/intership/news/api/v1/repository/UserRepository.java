package com.vnpt.intership.news.api.v1.repository;

import com.vnpt.intership.news.api.v1.domain.dto.User;
import com.vnpt.intership.news.api.v1.domain.entity.DeviceMeta;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, ObjectId> {
    Optional<User> findById(String id);

    void deleteById(String id);

    @Query(value = "{'authIdentity.deviceMetas':  {'$elemMatch': ?0}}")
    Optional<UserEntity> findByDeviceMeta(DeviceMeta deviceMeta);

    @Query("{'username': ?0 }")
    Optional<UserEntity> findByUsername(String username);

    @Query("{'email': ?0 }")
    Optional<UserEntity> findByEmail(String email);
}
