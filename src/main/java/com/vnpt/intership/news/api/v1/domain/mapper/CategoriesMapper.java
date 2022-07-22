package com.vnpt.intership.news.api.v1.domain.mapper;

import com.vnpt.intership.news.api.v1.domain.dto.Category;
import com.vnpt.intership.news.api.v1.domain.dto.request.CreateCategory;
import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CategoriesMapper extends BaseMapper<CategoriesEntity, Category> {
    private ArticleMapper articleMapper;

    @PostConstruct
    public void init() {
        this.articleMapper = new ArticleMapper();
    }
    @Override
    public CategoriesEntity convertToEntity(Category dto, Object... args) {
        CategoriesEntity categoriesEntity = new CategoriesEntity();
        if (dto != null) {
            BeanUtils.copyProperties(dto, categoriesEntity, "articles");
            categoriesEntity.setArticles(articleMapper.convertToEntitySet(dto.getArticles()));
        }
        return categoriesEntity;
    }

    @Override
    public Category convertToDto(CategoriesEntity entity, Object... args) {
        Category category = new Category();
        if (entity != null) {
            BeanUtils.copyProperties(entity, category, "articles");
            if (entity.getArticles() != null) {
                category.setArticles(articleMapper.convertToDtoList(entity.getArticles()));
            }
        }
        return category;
    }

    public CategoriesEntity convertToEntity(CreateCategory dto, Object... args) {
        CategoriesEntity categoriesEntity = new CategoriesEntity();
        if (dto != null) {
            BeanUtils.copyProperties(dto, categoriesEntity, "articles");
        }
        return categoriesEntity;
    }
}
