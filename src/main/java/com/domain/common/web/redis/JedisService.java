package com.domain.common.web.redis;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

public class JedisService implements InitializingBean {

    // inject the actual template
    public RedisTemplate<String, String> redisTemplate;

    public RedisMessageListenerContainer listenerContainer;

    // inject the template as ListOperations
    // can also inject as Value, Set, ZSet, and HashOperations
    public ValueOperations<String, String> valueOps;

    public ListOperations<String, String> listOps;

    public SetOperations<String, String> setOps;

    public ZSetOperations<String, String> zSetOps;

    public HashOperations<String, String, String> opsForHash;

    public RedisTemplate<String, String> getRedisTemplate() {

        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public RedisMessageListenerContainer getListenerContainer() {

        return listenerContainer;
    }

    public void setListenerContainer(RedisMessageListenerContainer listenerContainer) {

        this.listenerContainer = listenerContainer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        this.valueOps = redisTemplate.opsForValue();
        this.listOps = redisTemplate.opsForList();
        this.setOps = redisTemplate.opsForSet();
        this.zSetOps = redisTemplate.opsForZSet();
        this.opsForHash = redisTemplate.opsForHash();
    }
}
