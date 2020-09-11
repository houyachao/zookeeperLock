package cn.yachaozz.zookeeper.utils;

import cn.yachaozz.zookeeper.config.DefaultWatch;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @author HouYC
 * @create 2020-09-10-20:35
 */
public class ZkLockUtils {

    private static ZooKeeper zk;

    private static String address = "39.100.90.93:2181/testLock";

    private static DefaultWatch defaultWatch = new DefaultWatch();

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static ZooKeeper getZookeeper() {

        try {
            //创建Zookeeper
            defaultWatch.setCountDownLatch(countDownLatch);
            zk = new ZooKeeper(address, 1000, defaultWatch);
            countDownLatch.await();
            System.out.println(zk.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zk;
    }

}
