package com.gonanoio.redis.controller;

import com.gonanoio.redis.exception.ResourceNotFoundException;
import com.gonanoio.redis.model.HashEntry;
import com.gonanoio.redis.service.RedisService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class HashController {
    private final RedisService redisService;

    public HashController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping(value = "/maps/{key}")
    public Collection<HashEntry> all(@PathVariable String key) {
        return this.redisService.findAll(key);
    }

    @GetMapping(value = "/maps/{key}/{name}")
    public HashEntry getField(@PathVariable String key, @PathVariable String name) {
        return redisService.getHashEntry(key, name).orElseThrow(() -> new ResourceNotFoundException(name));
    }

    @PostMapping("/maps/{key}")
    public HashEntry saveHashEntry(@PathVariable String key, @RequestBody HashEntry hashEntry) {
        return this.redisService.save(key, hashEntry);
    }
}
