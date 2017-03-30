package com.domain.common.framework;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZkTest {

    public static void main(String[] args) throws Exception {

        ZooKeeper zk = new ZooKeeper("10.128.8.11:2181",
                30000, new Watcher() {
            // 监控所有被触发的事件
            public void process(WatchedEvent event) {

                System.out.println("已经触发了" + event.getType() + "事件！");
            }
        });
        System.out.println("i_0000000050-------->" + new String(zk.getData("/utils/framework/vip/i_0000000050", false, null)));
        // 取出子目录节点列表
        // System.out.println(zk.getChildren("/utils/framework/driver",true));
        // System.out.println("i_0000000042-------->" + new String(zk.getData("/utils/framework/vip/i_0000000042",false,null)));
        // System.out.println("i_0000000043-------->" + new String(zk.getData("/utils/framework/vip/i_0000000043",false,null)));
        // System.out.println("i_0000000044-------->" + new String(zk.getData("/utils/framework/vip/i_0000000044",false,null)));
        // System.out.println("i_0000000045-------->" + new String(zk.getData("/utils/framework/vip/i_0000000045",false,null)));

    }
}
