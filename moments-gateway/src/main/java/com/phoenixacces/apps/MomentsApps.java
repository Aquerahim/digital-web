package com.phoenixacces.apps;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.phoenixacces.apps"})

@EntityScan(basePackages = {"com.phoenixacces.apps"})

@ComponentScan(basePackages = {"com.phoenixacces.apps"})

@PropertySource(value = {
    "classpath:application-gateway.properties",
    "classpath:application-jpa.properties",
    "classpath:application-security.properties",
    "classpath:application-mail.properties",
    "classpath:application-jms.properties",
    "classpath:application-scheduler.properties"
})

@EnableTransactionManagement
@Slf4j
public class MomentsApps extends SpringBootServletInitializer {


    public static void main(String[] args) {

        SpringApplication.run(MomentsApps.class, args);

        log.info("| --------------------------[      MERCI SEIGNEUR POUR TON SOUTIEN      ]-------------------------");
    }
}
