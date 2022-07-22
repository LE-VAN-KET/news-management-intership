package com.vnpt.intership.news.api.v1.controller;

import com.vnpt.intership.news.api.v1.domain.dto.Category;
import com.vnpt.intership.news.api.v1.domain.dto.request.CreateCategory;
import com.vnpt.intership.news.api.v1.domain.entity.CategoriesEntity;
import com.vnpt.intership.news.api.v1.service.CategoriesService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@SecurityRequirement(name = "BearerAuth")
@RequestMapping("api/v1/categories")
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

    @PostMapping
    public ResponseEntity<?> addCategory(@Valid @RequestBody CreateCategory category ){
        // fix error api add category of author: HarryNguyen1712
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriesService.addCategory(category));
    }

    @PutMapping("/{categoryId}")
//    @Secured("hasAnyRole('ROLE_USER')")
    public ResponseEntity<?> editCategory(@PathVariable("categoryId") String categoryId,
                                          @Valid @RequestBody Category category) {
        return ResponseEntity.ok(categoriesService.updateCategoryById(new ObjectId(categoryId), category));
    }
}
