package com.domain.common.web.batch;

import com.domain.common.web.redis.BossJedisService;
import com.domain.common.utils.D;
import com.domain.common.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractBatchTaskProcessor implements ApplicationContextAware, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBatchTaskProcessor.class);

    /**
     * 任务状态
     */
    public static final int TASK_STATUS_SUCCESS = 1;

    public static final int TASK_STATUS_FAILED = 2;

    public static final int TASK_STATUS_PROGRESS = 3;

    //审核中
    public static final int TASK_STATUS_AUTITING = 4;

    //审核驳回
    public static final int TASK_STATUS_AUTI_BACK = 5;

    public int maxProcessor = 10;

    public int minProcessor = 5;

    int perThreadQueueSize = 1;

    int queueSize = 150;

    int workingIntervalTime = 1 * 60; // unit: seconds

    ThreadPoolExecutor executor;

    Lock lock = null;

    Condition lockCondition = null;

    @Autowired
    public BossJedisService jedisService;

    ApplicationContext applicationContext;

    public boolean debug;

    public boolean isStop = false;

    public boolean isDebug() {

        return debug;
    }

    public void setDebug(boolean debug) {

        this.debug = debug;
    }

    public boolean isStop() {

        return isStop;
    }

    public void setStop(boolean isStop) {

        this.isStop = isStop;
    }

    public void init() {

        if (maxProcessor > queueSize) {
            queueSize = maxProcessor * perThreadQueueSize;
        }
        executor = new ThreadPoolExecutor(minProcessor, maxProcessor, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(queueSize), new ThreadFactory() {

            SecurityManager s = System.getSecurityManager();

            private final ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

            private final AtomicInteger threadNumber = new AtomicInteger(1);

            private final String namePrefix = "BossTaskProcessor-thread-";

            public Thread newThread(Runnable r) {

                Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
                if (t.isDaemon())
                    t.setDaemon(false);
                if (t.getPriority() != Thread.NORM_PRIORITY)
                    t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

                try {
                    if (!executor.isShutdown()) {
                        executor.getQueue().put(r);
                    }
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });

        lock = new ReentrantLock();
        lockCondition = lock.newCondition();
    }

    protected void logExcpAndWait(Exception e) {

        logger.error("BossTaskProcessor Exception:" + e.getMessage(), e);
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e1) {
            logger.error(e1.getMessage(), e1);
        }
    }

    public <T> T takeObject(Class<T> clazz) {

        String json = jedisService.listOps.rightPop(getListWorkingKey(), 20, TimeUnit.SECONDS);
        if (json == null)
            return null;
        T entity = JsonHelper.fromJson(json, clazz);
        return entity;
    }

    public void pushObject(Object obj) {

        String josn = JsonHelper.toJson(obj);
        long rt = jedisService.listOps.leftPush(getListWorkingKey(), josn);
        logger.debug(String.format("jedisService.listOps.leftPush key %s: %s, %s", getListWorkingKey(), rt, obj));
    }

    protected void pushObjectForSafe(String key, String id, Object obj) {

        String json = JsonHelper.toJson(obj);
        jedisService.opsForHash.put(key, id, json);
    }

    protected void removeObjectForSafe(String key, String id) {

        jedisService.opsForHash.delete(key, id);
    }

    public void execute() {

        logger.info("Starting BossTaskProcessor with " + maxProcessor + " threads!");
        int retryCount = 0;
        while (retryCount < 5) {
            try {
                if (isStop) {
                    executor.shutdown();
                    try {
                        lock.lock();
                        lockCondition.signalAll();
                    } finally {
                        lock.unlock();
                    }
                    break;
                }
                final BatchTaskInfo info = takeObject(BatchTaskInfo.class);
                if (info == null)
                    continue;
                logger.debug(String.format("take info from queue: %s", info.toString()));
                executor.submit(new Runnable() {

                    @SuppressWarnings({"unchecked", "deprecation", "rawtypes"})
                    @Override
                    public void run() {

                        logger.debug(String.format("BossTaskTask: start a task: %s", info.toString()));
                        try {
                            if (!checkWaitIntervalTime(info)) {
                                pushObject(info);
                                return;
                            }
                            String taskClassName = info.getTaskClassName();
                            Class taskClass = ClassUtils.forName(taskClassName);
                            BatchTask batchTask = (BatchTask) applicationContext.getBean(taskClass);
                            updateTaskStatus(info.getId(), TASK_STATUS_PROGRESS);
                            int taskStatus = batchTask.run(info);
                            updateTaskStatus(info.getId(), taskStatus);
                        } catch (Exception e) {
                            updateTaskStatus(info.getId(), TASK_STATUS_FAILED, info.getFailedCount(), info.getFailedMsg());
                            logger.error(e.getMessage(), e);
                        }
                    }
                });
                // reset retryCount
                retryCount = 0;
            } catch (Exception e) {
                retryCount++;
                logExcpAndWait(e);
            } finally {

            }
        }
    }

    public abstract void updateProgress(int taskId, float progress);

    public void updateTaskStatus(int taskId, int status) {

        updateTaskStatus(taskId, status, 0, null);
    }

    public abstract void updateTaskStatus(int taskId, int status, int failedCount, String failedMsg);

    public abstract void submitTask(BatchTaskInfo info);

    /**
     * 检查时间是否到期可以执行task
     * @param info
     * @return true: 可执行, false: 不可执行
     * @throws Exception
     */
    public boolean checkWaitIntervalTime(BatchTaskInfo info) throws Exception {
        // if less than intervalTime, waiting
        int leftTime = ((int) (info.getStartTime() / 1000) - D.unixTime());
        boolean waitingFor = false;
        if (leftTime > 0) {
            if (leftTime > workingIntervalTime) {
                leftTime = workingIntervalTime;
            } else {
                waitingFor = true;
            }
            try {
                lock.lock();
                logger.debug(String.format("waiting for %d seconds, %s", leftTime, info));
                lockCondition.await(leftTime, TimeUnit.SECONDS);
            } finally {
                lock.unlock();
            }
        } else {
            waitingFor = true;
        }
        return waitingFor;
    }

    public int getMaxProcessor() {

        return maxProcessor;
    }

    public void setMaxProcessor(int maxProcessor) {

        this.maxProcessor = maxProcessor;
    }

    public void destroy() {
        // TODO 暂时关闭
        logger.info("Shutdown BossTaskProcessor starting...");
        this.isStop = true;
        try {
            executor.awaitTermination(10 * 60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("Shutdown BossTaskProcessor end!");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {

        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO 暂时关闭
        init();
        new Thread(new Runnable() {

            @Override
            public void run() {

                execute();

            }
        }, "BossTaskProcessor-execute-thread").start();

    }

    /**
     * 定义redis队列key
     * @return
     */
    public abstract String getListWorkingKey();

}
