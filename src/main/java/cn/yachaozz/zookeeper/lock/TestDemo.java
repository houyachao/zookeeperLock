package cn.yachaozz.zookeeper.lock;

import cn.yachaozz.zookeeper.config.WatchCallBack;
import cn.yachaozz.zookeeper.utils.ZkLockUtils;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import static sun.misc.PostVMInitHook.run;

/**
 * @author HouYC
 * @create 2020-09-10-20:24
 */
public class TestDemo {

    public static void main(String[] args) {

        //创建Zookeeper 链接
        ZooKeeper zookeeper = ZkLockUtils.getZookeeper();

        //创建10个线程，模拟多台机器
        for (int i = 0; i < 1; i++) {
            new Thread(){
                @Override
                public void run() {

                    WatchCallBack watchCallBack = new WatchCallBack();
                    watchCallBack.setZk(zookeeper);
                    String threadName = Thread.currentThread().getName();
                    watchCallBack.setThreadName(threadName);

                    //每一个线程抢锁
                    watchCallBack.tryLock();

                    //业务代码

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

        try {
            zookeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {

        }
    }
}
