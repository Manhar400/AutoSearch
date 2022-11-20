package com.example.demo.configuration;

import com.example.demo.mongo.model.response.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static com.example.demo.constants.AppConstants.*;

@Slf4j
@Configuration
public class RedisConfiguration {

    /**
     * We're creating a new LettuceConnectionFactory object, which is a RedisConnectionFactory object, which is a
     * ConnectionFactory object
     *
     * @return A connection factory for a Redis server.
     */
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
        redisStandaloneConfig.setHostName(REDIS_HOST);
        redisStandaloneConfig.setPort(REDIS_PORT);
        redisStandaloneConfig.setPassword(REDIS_PASSWORD);
        return new LettuceConnectionFactory(redisStandaloneConfig);
    }
    /**
     * We are creating a new RedisSerializationContext object, which is a builder for creating a serialization context. We
     * are using a StringRedisSerializer for the key, a GenericToStringSerializer for the value, a StringRedisSerializer
     * for the hash key, and a GenericJackson2JsonRedisSerializer for the hash value
     *
     * @param connectionFactory The connection factory to use.
     * @return A reactive redis template
     */
    @Bean
    public ReactiveRedisOperations<String, Output> redisOperations(LettuceConnectionFactory connectionFactory) {

        RedisSerializationContext<String, Output> serializationContext = RedisSerializationContext
                .<String, Output>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new GenericToStringSerializer<>(Output.class))
                .hashKey(new StringRedisSerializer())
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}