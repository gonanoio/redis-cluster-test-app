package com.gonanoio.redis.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisClientConfig {

    @Profile("!test")
    @Bean(destroyMethod = "shutdown")
    public ClientResources redisClientResources() {
        return DefaultClientResources.create();
    }


    @Bean
    public ClientOptions redisClientOptions() {

        final ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh()
                .enableAllAdaptiveRefreshTriggers()
                .refreshPeriod(Duration.ofSeconds(30))
                .build();

        return ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions)
                .autoReconnect(true)
                .socketOptions(SocketOptions.builder().
                        keepAlive(SocketOptions.KeepAliveOptions.builder().enable(true).build())
                        .build())
                .build();
    }

    @Bean
    public LettucePoolingClientConfiguration lettucePoolingClientConfiguration(
            final ClientOptions redisClientOptions,
            final ClientResources redisClientResources,
            final RedisProperties redisProperties) {

        return LettucePoolingClientConfiguration.builder()
                .commandTimeout(redisProperties.getReadTimeout())
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .poolConfig(redisProperties.getPoolConfig()).clientOptions(redisClientOptions)
                .clientResources(redisClientResources).build();
    }

    @Bean
    public RedisClusterConfiguration redisClusterConfiguration(final RedisProperties redisProperties) {
        return new RedisClusterConfiguration(redisProperties.getNodes());
    }

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory(final RedisClusterConfiguration redisClusterConfiguration, final LettucePoolingClientConfiguration lettucePoolingClientConfiguration) {
        final LettuceConnectionFactory factory = new LettuceConnectionFactory(redisClusterConfiguration, lettucePoolingClientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public StringRedisTemplate redisTemplate(final ObjectMapper objectMapper, final RedisConnectionFactory lettuceConnectionFactory) {
        final StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}

