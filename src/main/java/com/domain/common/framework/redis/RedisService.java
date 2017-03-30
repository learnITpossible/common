package com.domain.common.framework.redis;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author yejianzhong
 * @date 2015-4-28
 */
public interface RedisService {

    /**
     * 根据传入的key获取对应的RedisTemplate
     * @param key
     * @return
     */
    public StringRedisTemplate getRedisTemplate(String key);

    public void delete(String key);

    /**
     * 指量删除keys，因为分布式，需要hash算出redis server，因此是在客户端遍历删除的，效率比较低
     * @param keys
     */
    public void delete(Collection<String> keys);

    public boolean expire(String key, final long timeout, final TimeUnit unit);

    /**
     * value set，对应RedisTemplate.ValueOperations.set(K key, V value);
     * @param key
     * @param value
     */
    void set(String key, String value);

    /**
     * value set，对应RedisTemplate.ValueOperations.set(K key, V value, long timeout, TimeUnit unit);
     * @param key
     * @param value
     * @param timeout
     * @param unit
     */
    void set(String key, String value, long timeout, TimeUnit unit);

    /**
     * value get， 对应RedisTemplate.ValueOperations.get(K key);
     * @param key
     * @return
     */
    String get(String key);

    public Long incrby(String key, long delta);

    /**
     * 对应RedisTemplate.opsForValue().increment(key, delta);
     * @param key
     * @param delta
     * @return
     */
    /**
     * hash delete, 对应RedisTemplate.HashOperations.delete(H key, Object... hashKeys);
     * @param key
     * @param hashKeys
     */
    void hdel(String key, Object... hashKeys);

    /**
     * hash has key, 对应RedisTemplate.HashOperations.hasKey(H key, Object hashKey);
     * @param key
     * @param hashKey
     * @return
     */
    boolean hexists(String key, String hashKey);

    /**
     * hash get, 对应RedisTemplate.HashOperations.get(H key, Object hashKey);
     * @param key
     * @param hashKey
     * @return
     */
    String hget(String key, String hashKey);

    /**
     * 对应RedisTemplate.opsForHash().entries(key);
     * @param key
     * @return
     */
    public Map<Object, Object> hgetAll(String key);

    /**
     * hash set, 对应RedisTemplate.HashOperations.put(H key, HK hashKey, HV value);
     * @param key
     * @param hashKey
     * @param value
     */
    void hset(String key, String hashKey, String value);

    /**
     * 调用hset(String key, String hashKey, String value)方法
     * 再调用 expire(String key, final long timeout, final TimeUnit unit);
     * @param key
     * @param hashKey
     * @param value
     * @param timeout
     * @param unit
     */
    void hset(String key, String hashKey, String value, long timeout, TimeUnit unit);

    /**
     * 对就RedisTemplate.opsForSet().add(key, values);
     * @param key
     * @param values
     */
    public void sadd(String key, String... values);

    /**
     * 对应RedisTemplate.opsForSet().remove(key, values);
     * @param key
     * @param values
     */
    public void srem(String key, String... values);

    /**
     * RedisTemplate.opsForSet().isMember(key, value);
     * @param key
     * @param value
     * @return
     */
    public boolean sismember(String key, String value);

    /**
     * RedisTemplate.opsForSet().isMember(key, value);
     * @param key
     * @return
     */
    public Set<String> smembers(String key);

    /**
     * 对应RedisTemplate.opsForList().leftPush(key, value);
     * @param key
     * @param value
     * @return
     */
    public Long lpush(String key, String value);

    /**
     * 对应RedisTemplate.opsForList().range(key, start, end);
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> lrange(String key, long start, long end);

    /**
     * 对应RedisTemplate.opsForList().rightPop(key, timeout, unit);
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public String brpop(String key, long timeout, TimeUnit unit);

    Long hLen(String key);
}
