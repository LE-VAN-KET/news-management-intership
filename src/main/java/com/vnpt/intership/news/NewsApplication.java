package com.vnpt.intership.news;

import com.vnpt.intership.news.api.v1.common.UserRole;
import com.vnpt.intership.news.api.v1.domain.entity.RoleEntity;
import com.vnpt.intership.news.api.v1.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.List;

@SpringBootApplication
@EnableMongoAuditing
public class NewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsApplication.class, args);
    }

    @Bean
    CommandLineRunner init(RoleRepository roleRepository) {

        return args -> {
            List<RoleEntity> roleEntities = roleRepository.findAll();

            if (roleEntities.isEmpty()) {
                RoleEntity roleAdmin = new RoleEntity();
                roleAdmin.setRoleName(UserRole.ROLE_ADMIN);
                RoleEntity roleUser = new RoleEntity();
                roleUser.setRoleName(UserRole.ROLE_USER);
                roleRepository.insert(List.of(roleAdmin, roleUser));
            }
        };

    }

}
