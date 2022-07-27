package com.vnpt.intership.news.api.v1.service.impl;

import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import com.vnpt.intership.news.api.v1.repository.CategoriesRepository;
import com.vnpt.intership.news.api.v1.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
@Service
public class CategoriesServiceImpl implements CategoriesService {
    @Autowired
    CategoriesRepository categoriesRepository;

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
}
