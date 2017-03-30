package com.domain.common.web.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BatchTask {

    private static final Logger logger = LoggerFactory.getLogger(BatchTask.class);

    @Autowired
    AbstractBatchTaskProcessor processor;

    public void updateProgress(int taskId, float progress) {

        processor.updateProgress(taskId, progress);
    }

    public int run(BatchTaskInfo info) throws Exception {

        return doRun(info);
    }

    /**
     * @return task status:
     * 1: BossTaskProcessor.TASK_STATUS_SUCCESS 成功
     * 2: BossTaskProcessor.TASK_STATUS_FAILED 失败
     * 3: BossTaskProcessor.TASK_STATUS_PROGRESS 处理中
     * @throws Exception
     */
    protected abstract int doRun(BatchTaskInfo info) throws Exception;

}
