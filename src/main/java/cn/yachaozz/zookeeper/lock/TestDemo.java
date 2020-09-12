package cn.yachaozz.zookeeper.lock;

import cn.yachaozz.zookeeper.config.WatchCallBack;
import cn.yachaozz.zookeeper.utils.ZkLockUtils;
import org.apache.zookeeper.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static sun.misc.PostVMInitHook.run;

/**
 * @author HouYC
 * @create 2020-09-10-20:24
 */
public class TestDemo {

    ZooKeeper zk;

    @Before
    public void conn (){
        zk  = ZkLockUtils.getZookeeper();
    }

    @After
    public void close (){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
//        ZooKeeper zooKeeper1 = new ZooKeeper("39.100.90.93:2181", 1000, new Watcher() {
//            @Override
//            public void process(WatchedEvent watchedEvent) {
//                if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
//                    //这里是创建完链接后，先阻塞一下，因为客户端连接Zookeeper会有一些延时，所以先阻塞一下
//                    System.out.println("无阻塞了  ");
//                    countDownLatch.countDown();
//                }
//            }
//        });
//        countDownLatch.await();
//
//
//        zooKeeper1.create("/hyc", "wycj1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
//                CreateMode.EPHEMERAL_SEQUENTIAL, new AsyncCallback.StringCallback() {
//                    @Override
//                    public void processResult(int i, String s, Object o, String s1) {
//                        if (s1 != null) {
//                            System.out.println(s1);
//                        }
//                    }
//                }, "cc");

//

        //创建Zookeeper 链接
//        ZooKeeper zookeeper = ZkLockUtils.getZookeeper();
        //创建10个线程，模拟多台机器
        for (int i = 0; i < 10; i++) {
            new Thread(){
                @Override
                public void run() {
                    String threadName = Thread.currentThread().getName();
                    // 创建有序的节点
                    WatchCallBack watchCallBack = new WatchCallBack();
                    watchCallBack.setZk(zk);
                    watchCallBack.setThreadName(threadName);

                    //每一个线程抢锁
                    watchCallBack.tryLock();
                    //业务代码
                    System.out.println("干活。。。。。。");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //释放锁
                    watchCallBack.unLock();
                }
            }.start();
        }

        while (true) {

        }
    }
}
