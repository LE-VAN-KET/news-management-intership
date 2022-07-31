package com.vnpt.intership.news.api.v1.service.impl;

import com.vnpt.intership.news.api.v1.domain.dto.Category;
import com.vnpt.intership.news.api.v1.domain.dto.request.CreateCategory;
import com.vnpt.intership.news.api.v1.domain.entity.ArticleEntity;
import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import com.vnpt.intership.news.api.v1.domain.mapper.CategoriesMapper;
import com.vnpt.intership.news.api.v1.exception.CategoryException;
import com.vnpt.intership.news.api.v1.repository.CategoriesRepository;
import com.vnpt.intership.news.api.v1.service.CategoriesService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class CategoriesServiceImpl implements CategoriesService {
    @Autowired
    CategoriesRepository categoriesRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private CategoriesMapper categoriesMapper;

    @Override
    public void deleteById(ObjectId id) {
        CategoriesEntity entity= categoriesRepository.findById(id).get();
        Set<ArticleEntity> articleEntityList = categoriesRepository.findById(id).get().getArticles();
        categoriesRepository.deleteById(id);
        for(ArticleEntity articleEntity:articleEntityList) {
            mongoTemplate.update(ArticleEntity.class)
                    .matching(where("id").is(articleEntity.getId()))
                    .apply(new Update().pull("categories", entity))
                    .first();
        }
    }


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
    public List<CategoriesEntity> getAll() {
        return categoriesRepository.findAll();
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

    @Override
    public List<CategoriesEntity> findCategoriesByCategoryKeys(List<String> categoryKeys) {
        return categoriesRepository.findAllByCategoryKey(categoryKeys);
    }
}
