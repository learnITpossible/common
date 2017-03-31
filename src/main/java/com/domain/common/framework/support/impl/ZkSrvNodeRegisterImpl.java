package com.domain.common.framework.support.impl;

import com.domain.common.framework.support.ShutdownHookCase;
import com.domain.common.framework.support.SrvNodeRegister;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 注册到Zookeeper的实现
 * @author Frank
 */
public class ZkSrvNodeRegisterImpl implements SrvNodeRegister {

    private static final Logger logger = LoggerFactory.getLogger(ZkSrvNodeRegisterImpl.class);

    private CuratorFramework zookeeper;

    private String configPath;  // 节点路径,namespace + configPath 为目标节点的绝对路径

    private String currNodeId;

    public void setConfigPath(String configPath) {

        this.configPath = configPath;
    }

    public ZkSrvNodeRegisterImpl() {

    }

    public ZkSrvNodeRegisterImpl(CuratorFramework zookeeper) {

        this.zookeeper = zookeeper;
    }

    public void setZookeeper(CuratorFramework zookeeper) {

        this.zookeeper = zookeeper;
    }

    @Override
    public void register(final String address) throws Exception {

        if (zookeeper.getState() == CuratorFrameworkState.LATENT) {
            zookeeper.start();
            zookeeper.newNamespaceAwareEnsurePath(configPath);
        }

        createNode(address);

        String zkNodePath = configPath + "/" + currNodeId;

        // 注册关闭钩子
        logger.info("--Register framework node shutdown hook--");
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHookCase(zookeeper, zkNodePath)));

        zookeeper.getConnectionStateListenable().addListener(new ConnectionStateListener() {

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {

                if (newState == ConnectionState.RECONNECTED) {
                    try {
                        List<String> nodes = zookeeper.getChildren().forPath(configPath);
                        logger.info(nodes.toString());
                        if (nodes == null || nodes.size() == 0 ||
                                (!StringUtils.isBlank(currNodeId) && !nodes.contains(currNodeId))) {
                            createNode(address);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                logger.info("ConnectionState => " + newState.name());
            }

        });
    }

    public void createNode(String address) {

        try {
            currNodeId = zookeeper.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(configPath + "/i_", address.getBytes("utf-8"));

            if (!StringUtils.isBlank(currNodeId)) {
                currNodeId = StringUtils.substringAfterLast(currNodeId, "/");
            }

            logger.info("Register to ZK, NodeId is " + currNodeId + " , Address is " + address);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void close() {
        // nothing to do, 放到关闭钩子函数处理
    }

}
