package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.dto.Article;
import com.vnpt.intership.news.api.v1.domain.dto.request.CreateArticle;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public interface ArticleService {
    Article addArticle(CreateArticle createArticle, MultipartFile thumbnail);
    Article updateArticleById(ObjectId id, Article article);

    boolean existArticleByTitle(String title);
}
