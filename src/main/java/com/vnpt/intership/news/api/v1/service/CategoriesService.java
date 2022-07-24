package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.dto.Category;
import com.vnpt.intership.news.api.v1.domain.dto.request.CreateCategory;
import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import org.bson.types.ObjectId;

import java.util.List;

public interface CategoriesService {
    void save(CategoriesEntity categories);
    CategoriesEntity findByName(String name);

    List<CategoriesEntity> getAll();

    void deleteById(ObjectId id);
    Category addCategory(CreateCategory category);
    Category updateCategoryById(ObjectId id, Category category);

    boolean existCategoryByCategoryKey(String categoryKey);

    List<CategoriesEntity> findCategoriesByCategoryKeys(List<String> categoryKeys);

}
