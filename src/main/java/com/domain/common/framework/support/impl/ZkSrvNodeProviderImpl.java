package com.domain.common.framework.support.impl;

import com.domain.common.framework.support.SrvNodeProvider;
import com.domain.common.framework.support.SrvNodeProviderChangeListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.net.InetSocketAddress;
import java.util.*;


/**
 * 从Zookeeper中取得Srv节点的provider
 * @author chenmz
 */
public class ZkSrvNodeProviderImpl implements SrvNodeProvider, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ZkSrvNodeProviderImpl.class);

    private String configPath;

    private PathChildrenCache cachedPath;

    private CuratorFramework zookeeper;

    /**
     * 历史Node列表的跟踪
     */
    private Set<String> trace = new HashSet<String>();

    private final List<InetSocketAddress> container = new ArrayList<InetSocketAddress>();

    private Queue<InetSocketAddress> inner = new LinkedList<InetSocketAddress>();

    private Object lock = new Object();

    private SrvNodeProviderChangeListener listener;

    private static final Integer DEFAULT_PRIORITY = 1;

    public void setConfigPath(String configPath) {

        this.configPath = configPath;
    }

    public void setZookeeper(CuratorFramework zookeeper) {

        this.zookeeper = zookeeper;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (zookeeper.getState() == CuratorFrameworkState.LATENT) {// start zk
            zookeeper.start();
        }
        buildPathChildrenCache(zookeeper, configPath, true);
        cachedPath.start(StartMode.POST_INITIALIZED_EVENT);
    }

    private void buildPathChildrenCache(CuratorFramework client, String path, Boolean cacheData) throws Exception {

        cachedPath = new PathChildrenCache(client, path, cacheData);
        cachedPath.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

                PathChildrenCacheEvent.Type eventType = event.getType();
                switch (eventType) {
                    case INITIALIZED:
                        return;
                    case CONNECTION_SUSPENDED:
                    case CONNECTION_LOST:
                        logger.info("ZK_CONN: CONNECTION LOST!");
                        return;
                    default:
                }
                cachedPath.rebuild();
                rebuild(eventType);
            }

            protected void rebuild(PathChildrenCacheEvent.Type eventType) throws Exception {

                List<ChildData> children = cachedPath.getCurrentData();
                logger.info(eventType.name() + " => " + children.toString());
                if ((eventType != PathChildrenCacheEvent.Type.INITIALIZED) && (children == null || children.isEmpty())) {
                    container.clear();
                    logger.info("ZK_CONN: Service not found. " + eventType.name());
                    return;
                }
                List<InetSocketAddress> current = new ArrayList<InetSocketAddress>();
                // Frank: 这里将来需要做成，当发现current.size大于以前的current的个数时，通知ClientProxyFactory.java分流
                Set<String> newTrace = new HashSet<String>();
                for (ChildData data : children) {
                    String address = new String(data.getData(), "utf-8");
                    current.addAll(transfer(address));
                    newTrace.add(address);
                }
                Collections.shuffle(current);

                // Frank: 只有新增节点，才通知外部重建链接，以像运行时分流，如果删除节点就不做，以免当srv node与zookeeper之前发生瞬断时，引起所有srv client重建连接池，形成震荡
                // Frank: 2014.05.07 修改成只要有节点变化，就通知连接池变化，网络不稳定的时候，有可能造成震荡.
                // if(listener != null &&  trace.size() > 0 &&  !trace.containsAll(newTrace)){
                if (listener != null && trace.size() > 0) {
                    if (eventType == PathChildrenCacheEvent.Type.CHILD_ADDED
                            || eventType == PathChildrenCacheEvent.Type.CHILD_UPDATED
                            || eventType == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                        logger.info("SrvNode Change:" + current.toString());
                        listener.onChanged(current);
                    }
                }
                // 备用，有可能部分srv node与zookeeper网络出问题
                trace.addAll(newTrace);

                synchronized (lock) {
                    container.clear();
                    container.addAll(current);
                    inner.clear();
                    inner.addAll(current);

                }
            }
        });
    }

    private List<InetSocketAddress> transfer(String address) {

        List<InetSocketAddress> result = new ArrayList<InetSocketAddress>();
        String[] hostname = address.split(":");
        Integer priority = DEFAULT_PRIORITY;
        if (hostname.length == 3) {
            priority = Integer.valueOf(hostname[2]);
        }

        if (hostname.length >= 2) {
            String ip = hostname[0];
            Integer port = Integer.valueOf(hostname[1]);
            for (int i = 0; i < priority; i++) {
                result.add(new InetSocketAddress(ip, port));
            }
        }
        return result;
    }

    @Override
    public List<InetSocketAddress> getAll() {

        return Collections.unmodifiableList(container);
    }

    @Override
    public synchronized InetSocketAddress get() {

        if (inner.isEmpty()) {
            if (!container.isEmpty()) {
                inner.addAll(container);
            } else if (!trace.isEmpty()) {
                synchronized (lock) {
                    for (String hostname : trace) {
                        container.addAll(transfer(hostname));
                    }
                    Collections.shuffle(container);
                    inner.addAll(container);
                }
            }
        }
        return inner.poll();// null
    }

    @Override
    public void close() {

    }

    @Override
    public void setSrvNodeProviderChangeListener(SrvNodeProviderChangeListener listener) {

        this.listener = listener;
    }

}
