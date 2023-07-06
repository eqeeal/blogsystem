package com.example.blogsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//在Java配置类中创建RedisTemplate bean，以便在应用程序中使用Redis。
@Configuration
public class RedisConfig {
    /*
     * JedisConnectionFactory 是 Spring Data Redis 提供的一个 Redis 连接工厂，用于创建 Jedis 连接。Jedis 是 Redis 的 Java 客户端之一，通过 JedisConnectionFactory 创建的 Jedis 连接可以用于在 Spring Boot 项目中操作 Redis 数据库*/
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("localhost", 6379);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    /*使用Redis的对象*/
    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //key采用String的序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
