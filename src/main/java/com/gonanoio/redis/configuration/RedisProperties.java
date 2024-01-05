package com.gonanoio.redis.configuration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;


@Validated
@ConfigurationProperties(prefix = "redis.config")
public class RedisProperties {
    private Duration readTimeout;
    private List<String> nodes;
    private int maxTotalPool;
    private int maxIdlePool;
    private int minIdlePool;
    private Duration maxWait;

    public <T> GenericObjectPoolConfig<T> getPoolConfig() {
        GenericObjectPoolConfig<T> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(maxTotalPool);
        config.setMaxIdle(maxIdlePool);
        config.setMinIdle(minIdlePool);
        config.setMaxWait(maxWait);
        return config;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public void setMaxTotalPool(int maxTotalPool) {
        this.maxTotalPool = maxTotalPool;
    }

    public void setMaxIdlePool(int maxIdlePool) {
        this.maxIdlePool = maxIdlePool;
    }

    public void setMinIdlePool(int minIdlePool) {
        this.minIdlePool = minIdlePool;
    }

    public void setMaxWait(Duration maxWait) {
        this.maxWait = maxWait;
    }
}