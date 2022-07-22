package com.vnpt.intership.news.api.v1.domain.mapper;

import com.vnpt.intership.news.api.v1.domain.dto.Article;
import com.vnpt.intership.news.api.v1.domain.entity.ArticleEntity;
import org.springframework.beans.BeanUtils;

import javax.annotation.PostConstruct;

public class ArticleMapper extends BaseMapper<ArticleEntity, Article> {
    private CategoriesMapper categoriesMapper;

    @PostConstruct
    public void init() {
        this.categoriesMapper = new CategoriesMapper();
    }
    @Override
    public ArticleEntity convertToEntity(Article dto, Object... args) {
        ArticleEntity articleEntity = new ArticleEntity();
        if (dto != null) {
            BeanUtils.copyProperties(dto, articleEntity, "categories");
            articleEntity.setCategories(categoriesMapper.convertToEntitySet(dto.getCategories()));
        }
        return articleEntity;
    }

    @Override
    public Article convertToDto(ArticleEntity entity, Object... args) {
        Article article = new Article();
        if (entity != null) {
            BeanUtils.copyProperties(entity, article, "categories");
            article.setCategories(categoriesMapper.convertToDtoList(entity.getCategories()));
        }
        return article;
    }
}
