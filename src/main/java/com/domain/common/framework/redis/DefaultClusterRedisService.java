package com.domain.common.framework.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 默认redis集群服务实现
 * <p>
 * modified by @author yejianzhong  @date 2015-4-28
 */
public class DefaultClusterRedisService implements RedisService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultClusterRedisService.class);

    private String servers = null;

    private List<StringRedisTemplate> templateList = new ArrayList<StringRedisTemplate>();

    private JedisPoolConfig jedisPoolConfig;

    private int timeout;

    private String password;

    private int serverSize = 0;

    private int database;

    public void init() {

        logger.info("DefaultClusterRedisService init starting...");
        if (servers == null || servers.length() == 0) {
            throw new IllegalArgumentException("the redis server list is empty !");
        }

        if (jedisPoolConfig == null) {
            throw new IllegalArgumentException("the jedisPoolConfig is null !");
        }

        initTemplateList(servers);
        logger.info("DefaultClusterRedisService init success, servers: " + servers + " ->templateList: " + templateList);
    }

    private void initTemplateList(String servers) {

        String[] serversArr = StringUtils.tokenizeToStringArray(servers, ",; ");

        for (String server : serversArr) {
            String args[] = server.split(":");
            if (args.length != 2 && args.length != 3) {
                throw new IllegalArgumentException("the server format is wrong:" + server);
            }

            JedisConnectionFactory jf = new JedisConnectionFactory(jedisPoolConfig);
            jf.setUsePool(true);
            jf.setHostName(args[0]);
            jf.setPort(Integer.valueOf(args[1]));
            jf.setTimeout(timeout);
            jf.setDatabase(database);
            if (password != null) {
                jf.setPassword(password);
            }
            jf.afterPropertiesSet();
            StringRedisTemplate template = new StringRedisTemplate();
            template.setConnectionFactory(jf);
            template.afterPropertiesSet();
            templateList.add(template);
        }
        serverSize = templateList.size();
    }

    @Override
    public StringRedisTemplate getRedisTemplate(String key) {

        int hash = (key.hashCode() & 0x7FFFFFFF);
        int index = hash % serverSize;
        return templateList.get(index);
    }

    public void setServers(String servers) {

        this.servers = servers;
    }

    public void setTimeout(int timeout) {

        this.timeout = timeout;
    }

    public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {

        this.jedisPoolConfig = jedisPoolConfig;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public void setDatabase(int database) {

        this.database = database;
    }

    @Override
    public void delete(String key) {

        getRedisTemplate(key).delete(key);

    }

    @Override
    public void delete(Collection<String> keys) {

        for (String key : keys) {
            delete(key);
        }
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {

        return getRedisTemplate(key).expire(key, timeout, unit);
    }

    @Override
    public void set(String key, String value) {

        getRedisTemplate(key).opsForValue().set(key, value);

    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {

        getRedisTemplate(key).opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public String get(String key) {

        return getRedisTemplate(key).opsForValue().get(key);
    }

    public Long incrby(String key, long delta) {

        return getRedisTemplate(key).opsForValue().increment(key, delta);
    }

    @Override
    public void hdel(String key, Object... hashKeys) {

        getRedisTemplate(key).opsForHash().delete(key, hashKeys);
    }

    @Override
    public boolean hexists(String key, String hashKey) {

        return getRedisTemplate(key).opsForHash().hasKey(key, hashKey);
    }

    @Override
    public String hget(String key, String hashKey) {

        return (String) getRedisTemplate(key).opsForHash().get(key, hashKey);
    }

    public Map<Object, Object> hgetAll(String key) {

        return getRedisTemplate(key).opsForHash().entries(key);
    }

    @Override
    public void hset(String key, String hashKey, String value) {

        getRedisTemplate(key).opsForHash().put(key, hashKey, value);
    }

    @Override
    public void hset(String key, String hashKey, String value, long timeout,
                     TimeUnit unit) {

        hset(key, hashKey, value);
        expire(key, timeout, unit);
    }

    public void sadd(String key, String... values) {

        getRedisTemplate(key).opsForSet().add(key, values);
    }

    public void srem(String key, String... values) {

        getRedisTemplate(key).opsForSet().remove(key, values);
    }

    public boolean sismember(String key, String value) {

        return getRedisTemplate(key).opsForSet().isMember(key, value);
    }

    public Set<String> smembers(String key) {

        return getRedisTemplate(key).opsForSet().members(key);
    }

    public Long lpush(String key, String value) {

        return getRedisTemplate(key).opsForList().leftPush(key, value);
    }

    public List<String> lrange(String key, long start, long end) {

        return getRedisTemplate(key).opsForList().range(key, start, end);
    }

    public String brpop(String key, long timeout, TimeUnit unit) {

        return getRedisTemplate(key).opsForList().rightPop(key, timeout, unit);
    }

    @Override
    public Long hLen(String key) {

        return getRedisTemplate(key).opsForHash().size(key);
    }

}
