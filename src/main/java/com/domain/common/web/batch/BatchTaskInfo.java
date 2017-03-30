package com.domain.common.web.batch;

public class BatchTaskInfo {

    long startTime;

    int id;

    int opType;

    int opId;

    float opProgress;

    String file;

    String opArgs;

    String opMsg;

    String opName;

    int failedCount;

    String failedMsg;

    String taskClassName;

    int totalCount;

    public long getStartTime() {

        return startTime;
    }

    public void setStartTime(long startTime) {

        this.startTime = startTime;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getOpType() {

        return opType;
    }

    public void setOpType(int opType) {

        this.opType = opType;
    }

    public String getFile() {

        return file;
    }

    public void setFile(String file) {

        this.file = file;
    }

    public String getOpArgs() {

        return opArgs;
    }

    public void setOpArgs(String opArgs) {

        this.opArgs = opArgs;
    }

    public String getTaskClassName() {

        return taskClassName;
    }

    public void setTaskClassName(String taskClassName) {

        this.taskClassName = taskClassName;
    }

    public int getOpId() {

        return opId;
    }

    public void setOpId(int opId) {

        this.opId = opId;
    }

    public String getOpMsg() {

        return opMsg;
    }

    public void setOpMsg(String opMsg) {

        this.opMsg = opMsg;
    }

    public float getOpProgress() {

        return opProgress;
    }

    public void setOpProgress(float opProgress) {

        this.opProgress = opProgress;
    }

    public String getOpName() {

        return opName;
    }

    public void setOpName(String opName) {

        this.opName = opName;
    }

    public int getFailedCount() {

        return failedCount;
    }

    public void setFailedCount(int failedCount) {

        this.failedCount = failedCount;
    }

    public String getFailedMsg() {

        return failedMsg;
    }

    public void setFailedMsg(String failedMsg) {

        this.failedMsg = failedMsg;
    }

    public int getTotalCount() {

        return totalCount;
    }

    public void setTotalCount(int totalCount) {

        this.totalCount = totalCount;
    }

    @Override
    public String toString() {

        return "BossTaskInfo [startTime=" + startTime + ", id=" + id
                + ", opType=" + opType + ", opId=" + opId + ", opProgress="
                + opProgress + ", file=" + file + ", opArgs=" + opArgs
                + ", opMsg=" + opMsg + ", opName=" + opName + ", failedCount="
                + failedCount + ", failedMsg=" + failedMsg + ", taskClassName="
                + taskClassName + ", totalCount=" + totalCount + "]";
    }

}
