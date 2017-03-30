package com.domain.common.framework.support.impl;

import com.domain.common.framework.support.SrvNodeProvider;
import com.domain.common.framework.support.SrvNodeProviderChangeListener;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 从Zookeeper中取得Srv节点
 * @author chenmz
 */
public class SimpleSrvNodeProviderImpl implements SrvNodeProvider {

    private final List<InetSocketAddress> container = new CopyOnWriteArrayList<InetSocketAddress>();

    private Queue<InetSocketAddress> inner = new LinkedList<InetSocketAddress>();

    public SimpleSrvNodeProviderImpl() {

    }

    /**
     * @param serverAddress "ip:port:priority;ip:port"
     */
    public SimpleSrvNodeProviderImpl(String serverAddress) {

        String[] hostnames = serverAddress.split(";");
        for (String hostname : hostnames) {
            String[] address = hostname.split(":");
            InetSocketAddress sa = new InetSocketAddress(address[0], Integer.valueOf(address[1]));
            Integer priority = 1;
            if (address.length == 3) {
                priority = Integer.valueOf(address[2]);
            }
            for (int i = 0; i < priority; i++) {
                container.add(sa);
            }
        }
        Collections.shuffle(container);
        inner.addAll(container);
    }

    @Override
    public List<InetSocketAddress> getAll() {

        return Collections.unmodifiableList(container);
    }

    @Override
    public synchronized InetSocketAddress get() {

        if (inner.isEmpty()) {
            inner.addAll(container);
        }
        return inner.poll();
    }

    @Override
    public void close() {

    }

    @Override
    public void setSrvNodeProviderChangeListener(SrvNodeProviderChangeListener listener) {

    }

}
