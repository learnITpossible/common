package com.domain.common.framework.support;

public interface SrvServerIpTransfer {

    public String getIp() throws Exception;

    public void reset();

    // 当IP变更时,将会调用reset方法
    static interface IpRestCalllBack {

        public void rest(String newIp);
    }

}
