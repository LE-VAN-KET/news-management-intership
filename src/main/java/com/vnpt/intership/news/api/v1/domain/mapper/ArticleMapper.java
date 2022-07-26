package com.vnpt.intership.news.api.v1.domain.mapper;

import com.vnpt.intership.news.api.v1.domain.dto.Article;
import com.vnpt.intership.news.api.v1.domain.entity.ArticleEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ArticleMapper extends BaseMapper<ArticleEntity, Article> {
    private CategoriesMapper categoriesMapper;
    private UserMapper userMapper;

    @PostConstruct
    public void init() {
        this.categoriesMapper = new CategoriesMapper();
        this.userMapper = new UserMapper();
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
            BeanUtils.copyProperties(entity, article, "categories", "user");
            article.setUser(userMapper.convertToDto(entity.getUser()));
            article.setCategories(categoriesMapper.convertToDtoList(entity.getCategories()));
        }
        return article;
    }
}
