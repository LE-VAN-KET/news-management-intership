package com.vnpt.intership.news.api.v1.repository;

import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, ObjectId> {
    @Query(value = "{'username':  ?0}", fields = "{'authIdentity': 0}")
    Optional<UserEntity> findByUsername(String username);

    @Query(value = "{'authIdentity.refreshToken':  ?0}")
    Optional<UserEntity> findByRefreshToken(String refreshToken);
}
