package com.domain.common.framework.lock;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GlobalLockRedisFactory implements GlobalLockFactory {

    private final static int LOCK_EXPIRED = 15 * 60 * 1000; // 15minutes

    private final ConcurrentHashMap<String, GlobalLock> locks = new ConcurrentHashMap<String, GlobalLock>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public RedisTemplate<String, String> globalLockRedisTemplate;

    public RedisTemplate<String, String> getGlobalLockRedisTemplate() {

        return globalLockRedisTemplate;
    }

    public void setGlobalLockRedisTemplate(
            RedisTemplate<String, String> globalLockRedisTemplate) {

        this.globalLockRedisTemplate = globalLockRedisTemplate;
    }

    @Override
    public GlobalLock getLock(String lockKey) {

        return getLock(lockKey, LOCK_EXPIRED);
    }

    public GlobalLock getLock(String lockKey, long aliveMill) {

        GlobalLock lock = locks.get(lockKey);
        if (lock != null) {
            return lock;
        }
        lock = new GlobalLock(globalLockRedisTemplate, lockKey, aliveMill);
        GlobalLock oldLock = locks.putIfAbsent(lockKey, lock);
        if (oldLock != null) {
            lock = oldLock;
        } else {
            scheduler.submit(newCleanLockTask(lockKey, aliveMill));
        }
        return lock;
    }

    public void removeLock(String lockKey) {

        locks.remove(lockKey);
    }

    private CleanLockTask newCleanLockTask(String lockKey, long aliveMill) {

        return new CleanLockTask(lockKey, aliveMill);
    }

    private class CleanLockTask implements Runnable {

        private long delayMill = 1000;

        private String key;

        private long waitMill;

        private long currentMill;

        private CleanLockTask(String key, long waitMill) {

            this.key = key;
            currentMill = System.currentTimeMillis();
            this.waitMill = waitMill;
        }

        @Override
        public void run() {

            if (waitMill > 0) {
                if (System.currentTimeMillis() - currentMill > waitMill) {
                    GlobalLock lock = locks.remove(key);
                    lock.unlock();
                } else {// 重新安排任务
                    delayMill += 100;
                    scheduler.schedule(this, delayMill, TimeUnit.MILLISECONDS);
                }
            } else {
                // nothing need to do
            }
        }
    }

}