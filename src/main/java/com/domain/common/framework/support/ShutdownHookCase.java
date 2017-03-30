package com.domain.common.framework.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;

/**
 * Created by ruanteng on 2015/5/8.
 */
public class ShutdownHookCase implements Runnable {

    private static final Log logger = LogFactory.getLog(ShutdownHookCase.class);

    private CuratorFramework zooKeeper;

    private String nodePath;

    public ShutdownHookCase(CuratorFramework zooKeeper, String nodePath) {

        this.zooKeeper = zooKeeper;
        this.nodePath = nodePath;
    }

    @Override
    public void run() {

        try {

            logger.info("---Close the service from zookeeper---");
            long st = System.currentTimeMillis();

            // 直接关闭实例，虽然可以达到关闭zk节点的效果，但同时也会影响正在使用实例远程访问接口的业务
            zooKeeper.delete().forPath(nodePath);
            logger.info("Deleted ZkNode from ZK:[" + nodePath + "]");

            Thread.sleep(5 * 1000); // 等待5秒结束，防止有客户端已经开始调用，但服务确退出了
            zooKeeper.close();
            logger.info("ZkNode from ZK:[" + nodePath + "] has stopped in " + (System.currentTimeMillis() - st) / 1000f + "s");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
