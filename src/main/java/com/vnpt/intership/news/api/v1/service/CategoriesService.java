package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.dto.Category;
import com.vnpt.intership.news.api.v1.domain.dto.request.CreateCategory;
import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import org.bson.types.ObjectId;

public interface CategoriesService {
    void save(CategoriesEntity categories);
    CategoriesEntity findByName(String name);
    Category addCategory(CreateCategory category);
    Category updateCategoryById(ObjectId id, Category category);

    boolean existCategoryByCategoryKey(String categoryKey);
}
