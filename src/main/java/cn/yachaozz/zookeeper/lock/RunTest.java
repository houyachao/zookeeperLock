package cn.yachaozz.zookeeper.lock;

import cn.yachaozz.zookeeper.utils.ZkLockUtils;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author HouYC
 * @create 2020-09-11-21:55
 */
public class RunTest implements Runnable {

    ZooKeeper zookeeper = ZkLockUtils.getZookeeper();

    @Override
    public void run() {
        for (int i = 0; i <10; i++) {
            String threadName = Thread.currentThread().getName();
            zookeeper.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL, new AsyncCallback.StringCallback() {
                        @Override
                        public void processResult(int i, String s, Object o, String s1) {
                            //如果创建的节点不为null，则创建成功
                            if (s1 != null) {
                                System.out.println(threadName + "创建 节点：" + s1);
//                                        pathName = s1;

                                //获取/testLock 下所有的节点，不监听
//                                zk.getChildren("/", false, this, "abc");
                            }
                        }
                    }, "cc");
        }
    }
}
