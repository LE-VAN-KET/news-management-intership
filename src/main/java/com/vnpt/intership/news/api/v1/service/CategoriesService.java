package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;

public interface CategoriesService {
    void save(CategoriesEntity categories);
    CategoriesEntity findByName(String name);
}
