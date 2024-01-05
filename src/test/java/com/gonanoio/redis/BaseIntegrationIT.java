package com.gonanoio.redis;

import com.gonanoio.redis.configuration.TestWebConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;



@TestPropertySource(locations = "classpath:application-test.yml")
@ContextConfiguration(classes = TestWebConfiguration.class, initializers = TestWebConfiguration.class)
@ActiveProfiles("test")
public class BaseIntegrationIT {}
