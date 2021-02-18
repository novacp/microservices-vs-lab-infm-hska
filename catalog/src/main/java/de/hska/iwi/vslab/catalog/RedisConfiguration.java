package de.hska.iwi.vslab.catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {

    @Autowired
    ServiceConfiguration serviceConfiguration;


    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConFactory
                = new JedisConnectionFactory();
        jedisConFactory.setHostName(serviceConfiguration.getRedis());
        jedisConFactory.setPassword("test");
        return jedisConFactory;
    }
}
