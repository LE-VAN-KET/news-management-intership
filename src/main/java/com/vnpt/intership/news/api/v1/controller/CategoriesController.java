package com.vnpt.intership.news.api.v1.controller;

import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import com.vnpt.intership.news.api.v1.service.CategoriesService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoriesController {
    @Autowired
    CategoriesService categoriesService;

    @RequestMapping(value = "/addCategory",method = RequestMethod.POST)
    public CategoriesEntity addCategory(@Valid @RequestBody CategoriesEntity category ){
        try {
            category.setId(ObjectId.get());
            category.setParent(categoriesService.findByName(category.getParent().getCategoryName()));
            categoriesService.save(category);
            return category;
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
}
