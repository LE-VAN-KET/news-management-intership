package com.vnpt.intership.news.api.v1.repository;

import com.vnpt.intership.news.api.v1.domain.entity.ArticleEntity;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends MongoRepository<ArticleEntity, ObjectId> {
    boolean existsByTitle(String title);

    @NotNull
    @Override
    @Aggregation(pipeline = {
            "{ $match:  {'_id':  ?0} }",
            "{ $lookup: {from:  'Categories', localField: 'categories.$id', foreignField:  '_id', as:  'categories'} }",
    })
    Optional<ArticleEntity> findById(@NotNull ObjectId id);
}
