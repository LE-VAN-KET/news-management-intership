package com.vnpt.intership.news.api.v1.controller;

import com.vnpt.intership.news.api.v1.domain.dto.request.CreateArticle;
import com.vnpt.intership.news.api.v1.service.ArticleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/articles")
@SecurityRequirement(name = "BearerAuth")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @PostMapping( consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ResponseEntity<?> addArticle(@Valid @RequestPart("article") CreateArticle createArticle,
                                        @RequestPart @Valid @NotNull @NotBlank MultipartFile thumbnail) {
        return ResponseEntity.status(201).body(articleService.addArticle(createArticle, thumbnail));
    }
}
