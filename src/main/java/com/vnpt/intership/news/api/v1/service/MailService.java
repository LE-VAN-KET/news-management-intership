package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.dto.EmailDTO;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

public interface MailService {
    void sendMimeMessage(EmailDTO emailDTO, Context ctx, String template) throws MessagingException;
}
