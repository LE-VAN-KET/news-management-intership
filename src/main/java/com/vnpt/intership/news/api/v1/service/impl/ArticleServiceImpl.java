package com.vnpt.intership.news.api.v1.service.impl;

import com.vnpt.intership.news.api.v1.domain.dto.Article;
import com.vnpt.intership.news.api.v1.domain.dto.Category;
import com.vnpt.intership.news.api.v1.domain.dto.request.CategoryArticle;
import com.vnpt.intership.news.api.v1.domain.dto.request.CreateArticle;
import com.vnpt.intership.news.api.v1.domain.entity.ArticleEntity;
import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import com.vnpt.intership.news.api.v1.domain.mapper.ArticleMapper;
import com.vnpt.intership.news.api.v1.exception.ArticleNotFoundException;
import com.vnpt.intership.news.api.v1.exception.CategoryException;
import com.vnpt.intership.news.api.v1.repository.ArticleRepository;
import com.vnpt.intership.news.api.v1.repository.CategoriesRepository;
import com.vnpt.intership.news.api.v1.service.ArticleService;
import com.vnpt.intership.news.api.v1.service.CategoriesService;
import com.vnpt.intership.news.api.v1.service.UserService;
import com.vnpt.intership.news.api.v1.util.MinioUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public Article addArticle(CreateArticle createArticle, MultipartFile thumbnail) {
        ArticleEntity articleEntity = new ArticleEntity();
        String thumbnailUrl = minioUtil.uploadFile(thumbnail);
        BeanUtils.copyProperties(createArticle, articleEntity, "thumbnail", "categories");
        articleEntity.setThumbnailUrl(thumbnailUrl);
        List<String> categoryKeys = createArticle.getCategories().stream()
                        .map(CategoryArticle::getCategoryKey).collect(Collectors.toList());
        articleEntity.setCategories(new HashSet<>(categoriesService.findCategoriesByCategoryKeys(categoryKeys)));

        articleEntity.setUser(userService.getCurrentUser());
        return articleMapper.convertToDto(articleRepository.save(articleEntity));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, CategoryException.class})
    public Article updateArticleById(ObjectId id, Article article) {
        // check article id exist
        ArticleEntity articleEntity = articleRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Category not found with id:" + id));

        //check title after changed
        validateTitleEdition(article.getTitle(), articleEntity);


        Set<CategoriesEntity> categoriesEntities = new HashSet<>();
        for(Category category: article.getCategories()){
            CategoriesEntity entity = categoriesRepository.findByCategoryKey(category.getCategoryKey()).get();
            categoriesEntities.add(entity);
        }
        // update 5 field article
        articleEntity.setCategories(categoriesEntities);
        articleEntity.setTitle(article.getTitle());
        articleEntity.setContent(article.getContent());
        articleEntity.setThumbnailUrl(article.getThumbnailUrl());
        articleEntity.setUpdateAt(LocalDateTime.now());

        return articleMapper.convertToDto(articleRepository.save(articleEntity));
    }


    @Override
    public boolean existArticleByTitle(String title) {
        return articleRepository.existsByTitle(title);
    }

    private void validateTitleEdition(String title, ArticleEntity articleEntity) {
        if (!articleEntity.getTitle().equals(title)) {
            boolean existCategoryKey = articleRepository.existsByTitle(title);
            if (existCategoryKey) {
                throw new CategoryException("Title " + title + " already exist");
            }
        }
    }

    @Override
    public Article getDetailArticle(ObjectId id) {
        ArticleEntity articleEntity = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Article with id :" + id + " not found"));
        return articleMapper.convertToDto(articleEntity);
    }
}
