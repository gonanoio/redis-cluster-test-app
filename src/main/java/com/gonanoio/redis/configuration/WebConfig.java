package com.gonanoio.redis.configuration;

import com.gonanoio.redis.model.HashEntry;
import com.gonanoio.redis.repository.RedisRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Configuration
public class WebConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);


    @Bean
    @Profile({"docker", "test"})
    public CommandLineRunner initRedisData(final RedisRepository repository) {
        return args -> {

            Thread.sleep(10000); // Delay for setup

            LOGGER.info("Preloading " + "Quotes");
            final String quoteKey = "quotes";
            repository.save(quoteKey, new HashEntry("Bilbo Baggins", "I don't know half of you half as well as I should like; and I like less than half of you half as well as you deserve"));
            repository.save(quoteKey, new HashEntry("Frodo Baggins", "It's Useless To Meet Revenge With Revenge: It Will Heal Nothing"));

            LOGGER.info("Preloading random keys to hash across cluster");
            AtomicInteger i = new AtomicInteger(1);
            Stream.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
                    .map(c -> Pair.of(c, i.getAndIncrement()))
                    .forEach(p ->
                            repository.save(p.getKey(), new HashEntry(String.valueOf(p.getValue()), String.valueOf(p.getValue() + 1))));

            LOGGER.info("Preloading complete");
        };
    }
}
