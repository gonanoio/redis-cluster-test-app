package com.gonanoio.redis.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String name) {
        super("Could not find resource " + name);
    }
}