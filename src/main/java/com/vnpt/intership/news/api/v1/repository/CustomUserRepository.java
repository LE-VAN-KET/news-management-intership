package com.vnpt.intership.news.api.v1.repository;

import com.vnpt.intership.news.api.v1.domain.entity.DeviceMeta;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class CustomUserRepository {
//    @Autowired
//    private MongoOperations mongoOperations;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void findAndUpdateRefreshTokenByUsername(String username, DeviceMeta deviceMeta) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username)
                .andOperator(Criteria.where("authIdentity.deviceMetas").elemMatch(
                        Criteria.where("deviceDetails").is(deviceMeta.getDeviceDetails())
                                .andOperator(Criteria.where("location").is(deviceMeta.getLocation()))
                )));

        Update update = new Update();
        update.set("authIdentity.deviceMetas.$", deviceMeta);

        mongoTemplate.findAndModify(query, update, UserEntity.class);
    }

    public void findAndUpdatePasswordResetCode(String username, String otp) {
        Query query = new Query(Criteria.where("username").is(username));
        Update update = new Update().set("authIdentity.passwordResetCode", otp);
        mongoTemplate.updateFirst(query, update, UserEntity.class);
    }
}
