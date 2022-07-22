package com.vnpt.intership.news.api.v1.service.impl;

import com.vnpt.intership.news.api.v1.domain.dto.Category;
import com.vnpt.intership.news.api.v1.domain.dto.request.CreateCategory;
import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import com.vnpt.intership.news.api.v1.domain.mapper.CategoriesMapper;
import com.vnpt.intership.news.api.v1.exception.CategoryException;
import com.vnpt.intership.news.api.v1.repository.CategoriesRepository;
import com.vnpt.intership.news.api.v1.service.CategoriesService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
@Service
public class CategoriesServiceImpl implements CategoriesService {
    @Autowired
    CategoriesRepository categoriesRepository;

    @Autowired
    private CategoriesMapper categoriesMapper;

    @Override
    public void save(CategoriesEntity categories) {
        if(categories.getArticles()==null) {
            categories.setArticles(new HashSet<>());
        }
        categoriesRepository.save(categories);
    }

    @Override
    public CategoriesEntity findByName(String name) {
        return categoriesRepository.findByCategoryName(name);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, CategoryException.class})
    public Category addCategory(CreateCategory category) {
        CategoriesEntity categoriesEntity = categoriesMapper.convertToEntity(category);
        // check parent null
        if (category.getParent() != null && category.getParent().getCategoryKey() != null) {
            CategoriesEntity parent = categoriesRepository.findByCategoryKey(category.getParent().getCategoryKey())
                    .orElseThrow(() -> new CategoryException("Category Key " + category.getParent().getCategoryKey()
                            + " not found with"));
            categoriesEntity.setParent(parent);
        }

        return categoriesMapper.convertToDto(categoriesRepository.save(categoriesEntity));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, CategoryException.class})
    public Category updateCategoryById(ObjectId id, Category category) {
        // check category id exist
        CategoriesEntity categoriesEntity = categoriesRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Category not found with id:" + id));

        //check category key after changed
        validateCategoryKeyEdition(category.getCategoryKey(), categoriesEntity);

        String categoryKeyParent = category.getParent().getCategoryKey();
        if (categoryKeyParent != null && !categoryKeyParent.equals(categoriesEntity.getParent().getCategoryKey())) {
            CategoriesEntity parent = categoriesRepository.findByCategoryKey(categoryKeyParent)
                    .orElseThrow(() -> new CategoryException("Category Key " + categoryKeyParent + " not found with"));
            categoriesEntity.setParent(parent);
        }

        // update 3 field category
        categoriesEntity.setCategoryKey(category.getCategoryKey());
        categoriesEntity.setCategoryName(category.getCategoryName());
        categoriesEntity.setDescription(category.getDescription());

        return categoriesMapper.convertToDto(categoriesRepository.save(categoriesEntity));
    }

    @Override
    public boolean existCategoryByCategoryKey(String categoryKey) {
        return categoriesRepository.existsByCategoryKey(categoryKey);
    }

    private void validateCategoryKeyEdition(String categoryKey, CategoriesEntity categoriesEntity) {
        if (!categoriesEntity.getCategoryKey().equals(categoryKey)) {
            boolean existCategoryKey = categoriesRepository.existsByCategoryKey(categoryKey);
            if (existCategoryKey) {
                throw new CategoryException("Category Key " + categoryKey + " already exist");
            }
        }
    }
}
