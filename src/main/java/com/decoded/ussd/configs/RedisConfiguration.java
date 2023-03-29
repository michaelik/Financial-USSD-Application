package com.decoded.ussd.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories

// this solution adds phantom keys in Redis and other app like python reading
// these keys will get two values for same key(1 original and 1 phantom)
public class RedisConfiguration extends CachingConfigurerSupport {

    @Value("${spring.cache.host}")
    private String host;

    @Value("${spring.cache.port}")
    private int port;

    /*@Value("${decoded.cache.password}")
    private String password;*/

    @Value("${spring.cache.default-ttl}")
    private String defaultTTL;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        //redisStandaloneConfiguration.setPassword(password);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

}
