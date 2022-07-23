package com.vnpt.intership.news.api.v1.service.impl;

import com.vnpt.intership.news.api.v1.domain.dto.Article;
import com.vnpt.intership.news.api.v1.domain.dto.Category;
import com.vnpt.intership.news.api.v1.domain.dto.request.CategoryArticle;
import com.vnpt.intership.news.api.v1.domain.dto.request.CreateArticle;
import com.vnpt.intership.news.api.v1.domain.entity.ArticleEntity;
import com.vnpt.intership.news.api.v1.domain.mapper.ArticleMapper;
import com.vnpt.intership.news.api.v1.repository.ArticleRepository;
import com.vnpt.intership.news.api.v1.service.ArticleService;
import com.vnpt.intership.news.api.v1.service.CategoriesService;
import com.vnpt.intership.news.api.v1.service.UserService;
import com.vnpt.intership.news.api.v1.util.MinioUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
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


}
