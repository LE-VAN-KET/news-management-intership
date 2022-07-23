package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.dto.Article;
import com.vnpt.intership.news.api.v1.domain.dto.request.CreateArticle;
import org.springframework.web.multipart.MultipartFile;


public interface ArticleService {
    Article addArticle(CreateArticle createArticle, MultipartFile thumbnail);
}
