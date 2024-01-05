package com.gonanoio.redis;

import com.gonanoio.redis.configuration.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = WebConfig.class)
public class RedisApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        LOGGER.debug("Printing spring beans");
        return args -> {
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                LOGGER.debug(beanName);
            }
        };
    }
}
