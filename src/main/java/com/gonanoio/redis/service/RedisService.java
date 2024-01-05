package com.gonanoio.redis.service;

import com.gonanoio.redis.model.HashEntry;
import com.gonanoio.redis.repository.RedisRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class RedisService {

    private final RedisRepository repository;

    public RedisService(RedisRepository repository) {
        this.repository = repository;
    }

    public Optional<HashEntry> getHashEntry(final String key, String name) {
        return repository.get(key, name);
    }
    public HashEntry save(final String key, HashEntry entry) {
        this.repository.save(key, entry);
        return entry;
    }
    public Collection<HashEntry> findAll(final String key) {
        return this.repository.findAll(key).values();
    }
}
