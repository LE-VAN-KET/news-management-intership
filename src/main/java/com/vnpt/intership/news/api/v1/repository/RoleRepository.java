package com.vnpt.intership.news.api.v1.repository;

import com.vnpt.intership.news.api.v1.common.UserRole;
import com.vnpt.intership.news.api.v1.domain.entity.RoleEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<RoleEntity, ObjectId> {
    @Query("{'roleName': ?0 }")
    Optional<RoleEntity> findByRoleName(String roleName);
}
