package com.vnpt.intership.news.api.v1.repository;

import com.vnpt.intership.news.api.v1.domain.entity.RoleEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<RoleEntity, ObjectId> {
}
