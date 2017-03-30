package com.domain.common.framework.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DefaultRedisService implements RedisService {

    public StringRedisTemplate redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {

        this.redisTemplate = new StringRedisTemplate(redisTemplate.getConnectionFactory());
    }

    @Override
    public StringRedisTemplate getRedisTemplate(String key) {

        return redisTemplate;
    }

    @Override
    public void delete(String key) {

        redisTemplate.delete(key);

    }

    @Override
    public void delete(Collection<String> keys) {

        redisTemplate.delete(keys);

    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {

        return redisTemplate.expire(key, timeout, unit);
    }

    @Override
    public void set(String key, String value) {

        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {

        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public String get(String key) {

        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Long incrby(String key, long delta) {

        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public void hdel(String key, Object... hashKeys) {

        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    @Override
    public boolean hexists(String key, String hashKey) {

        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public String hget(String key, String hashKey) {

        return (String) redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public Map<Object, Object> hgetAll(String key) {

        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public void hset(String key, String hashKey, String value) {

        redisTemplate.opsForHash().put(key, hashKey, value);

    }

    @Override
    public void hset(String key, String hashKey, String value, long timeout,
                     TimeUnit unit) {

        redisTemplate.opsForHash().put(key, hashKey, value);
        expire(key, timeout, unit);
    }

    @Override
    public void sadd(String key, String... values) {

        redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public void srem(String key, String... values) {

        redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public boolean sismember(String key, String value) {

        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Set<String> smembers(String key) {

        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Long lpush(String key, String value) {

        return redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public List<String> lrange(String key, long start, long end) {

        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public String brpop(String key, long timeout, TimeUnit unit) {

        return redisTemplate.opsForList().rightPop(key, timeout, unit);
    }

    @Override
    public Long hLen(String key) {

        return redisTemplate.opsForHash().size(key);
    }

}
