package com.gonanoio.redis.configuration;

import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.SocketAddressResolver;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@TestConfiguration
public class TestWebConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String DOCKER_IMAGE_NAME = "grokzen/redis-cluster:6.0.7";
    private static final Set<Integer> redisClusterPorts = Set.of(7000, 7001, 7002, 7003, 7004, 7005);
    private static final List<String> nodes = new ArrayList<>();
    private static final ConcurrentMap<Integer, Integer> redisClusterNotPortMapping = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, SocketAddress> redisClusterSocketAddresses = new ConcurrentHashMap<>();

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        redisTestSetup(applicationContext);
    }

    private void redisTestSetup(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        GenericContainer<?> redis = new GenericContainer<>(
                DockerImageName.parse(DOCKER_IMAGE_NAME))
                .withExposedPorts(redisClusterPorts.toArray(new Integer[0]));

        redis.start();
        String hostAddress = redis.getHost();
        redisClusterPorts.forEach(port -> {
            Integer mappedPort = redis.getMappedPort(port);
            redisClusterNotPortMapping.put(port, mappedPort);
            nodes.add(String.format("%s:%d", hostAddress, mappedPort));
        });
        setProperties(environment, nodes);
    }

    @Bean(destroyMethod = "shutdown")
    public ClientResources redisClientResources() {
        final SocketAddressResolver socketAddressResolver = new SocketAddressResolver() {
            @Override
            public SocketAddress resolve(RedisURI redisURI) {
                Integer mappedPort = redisClusterNotPortMapping.get(redisURI.getPort());
                if (mappedPort != null) {
                    SocketAddress socketAddress = redisClusterSocketAddresses.get(mappedPort);
                    if (socketAddress != null) {
                        return socketAddress;
                    }
                    redisURI.setPort(mappedPort);
                }
                redisURI.setHost(DockerClientFactory.instance().dockerHostIpAddress());
                SocketAddress socketAddress = super.resolve(redisURI);
                redisClusterSocketAddresses.putIfAbsent(redisURI.getPort(), socketAddress);
                return socketAddress;
            }
        };
        return ClientResources.builder().socketAddressResolver(socketAddressResolver).build();
    }

    private void setProperties(ConfigurableEnvironment environment, Object value) {
        MutablePropertySources sources = environment.getPropertySources();
        PropertySource<?> source = sources.get("redis.config.nodes");
        if (source == null) {
            source = new MapPropertySource("redis.config.nodes", new HashMap<>());
            sources.addFirst(source);
        }
        ((Map<String, Object>) source.getSource()).put("redis.config.nodes", value);
    }
}

