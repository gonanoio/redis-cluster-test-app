package com.gonanoio.redis.repository;

import com.gonanoio.redis.model.HashEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class RedisRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisRepository.class);

    private final StringRedisTemplate redisTemplate;

    public RedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<String, HashEntry> findAll(final String key) {
        final Map<Object, Object> results = redisTemplate.opsForHash().entries(key);
        return results.entrySet().stream()
                .map(entry -> new HashEntry((String) entry.getKey(), (String) entry.getValue()))
                .collect(Collectors.toMap(HashEntry::getName, Function.identity()));
    }
    public Optional<HashEntry> get(final String key, final String name) {
        final Object value = redisTemplate.opsForHash().get(key, name);
        return (value == null ? Optional.empty() : Optional.of(new HashEntry(name, String.valueOf(value))));
    }
    public void save(final String key, final HashEntry entry) {
        try{
            redisTemplate.opsForHash().put(key, entry.getName(), entry.getValue());
        }catch(Exception ex){
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
