package com.vnpt.intership.news.api.v1.repository;

import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriesRepository extends MongoRepository<CategoriesEntity, ObjectId> {
    CategoriesEntity findByCategoryName(String name);

    boolean existsByCategoryKey(String categoryKey);

    Optional<CategoriesEntity> findByCategoryKey(String categoryKey);

    @Query(value = "{ 'categoryKey' : {'$in' : ?0 } }", fields = "{'parent':  0, 'articles':  0}")
    List<CategoriesEntity> findAllByCategoryKey(List<String> categoryKeys);
}
