package com.vnpt.intership.news.api.v1.domain.dto.request;

import com.vnpt.intership.news.api.v1.domain.dto.Category;
import com.vnpt.intership.news.api.v1.util.validator.ValidArticleTitle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateArticle {
    @NotNull
    @NotEmpty
    @ValidArticleTitle
    private String title;

    @NotNull
    @NotEmpty(message = "content is required")
    @Size(max = 1024)
    private String content;

    @NotNull
    private List<CategoryArticle> categories = new ArrayList<>();
}
