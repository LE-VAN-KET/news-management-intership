package com.vnpt.intership.news.api.v1.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class EmailDTO {
    private List<String> recipients;
    private String subject;
    private String body;
}
