package com.domain.common.framework.support;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * 获取zookeeper客户端链接
 */
public class ZookeeperFactory implements FactoryBean<CuratorFramework> {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperFactory.class);

    private String connectString;

    private Integer sessionTimeout = 30000;

    // 共享一个zk链接
    private boolean singleton = true;

    // 全局path前缀,常用来区分不同的应用
    private String namespace;

    private CuratorFramework zkClient;

    public void setSessionTimeout(Integer sessionTimeout) {

        this.sessionTimeout = sessionTimeout;
    }

    public void setConnectString(String connectString) {

        this.connectString = connectString;
    }

    public void setSingleton(boolean singleton) {

        this.singleton = singleton;
    }

    public void setNamespace(String namespace) {

        this.namespace = namespace;
    }

    public CuratorFramework create() throws Exception {

        return create(connectString, sessionTimeout, namespace);
    }

    public static CuratorFramework create(String connectString, Integer sessionTimeout, String namespace) {

        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        return builder.connectString(connectString)
                .sessionTimeoutMs(30000)
                .connectionTimeoutMs(30000)
                .canBeReadOnly(true)
                .namespace(namespace)
                .retryPolicy(new ExponentialBackoffRetry(2000, Integer.MAX_VALUE))
                .defaultData(null)
                .build();
    }

    /**
     * client not started
     */
    @Override
    public synchronized CuratorFramework getObject() throws Exception {

        if (singleton) {
            if (zkClient == null) {
                zkClient = create();
                zkClient.start();
            }
            return zkClient;
        }
        return create();
    }

    @Override
    public Class<?> getObjectType() {

        return CuratorFramework.class;
    }

    @Override
    public boolean isSingleton() {

        return singleton;
    }

    public void close() {

        /*// 已经有注册关闭钩子处理关闭
        if (zkClient != null) {
            zkClient.close();
        }
        logger.info("----ZookeeperFactory closed-----");*/
    }

}
