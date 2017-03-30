package com.domain.common.framework.support;

import java.net.InetSocketAddress;
import java.util.List;

public interface SrvNodeProviderChangeListener {

    public void onChanged(List<InetSocketAddress> srvs);

}
