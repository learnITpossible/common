package com.domain.common.framework.support;

import java.net.InetSocketAddress;
import java.util.List;

public interface SrvNodeProvider {

    public List<InetSocketAddress> getAll();

    public InetSocketAddress get();

    public void close();

    public void setSrvNodeProviderChangeListener(SrvNodeProviderChangeListener listener);
}
