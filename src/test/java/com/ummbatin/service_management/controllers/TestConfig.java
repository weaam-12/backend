package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.repositories.ComplaintRepository;
import com.ummbatin.service_management.repositories.KindergartenRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootApplication(
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class,
                SecurityAutoConfiguration.class
        }
)
class TestConfig {

        @Bean
        @Primary
        public KindergartenRepository kindergartenRepository() {
                return Mockito.mock(KindergartenRepository.class);
        }

        @Bean
        @Primary
        public ComplaintRepository complaintRepository() {
                return Mockito.mock(ComplaintRepository.class);
        }
}
