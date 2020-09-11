package cn.yachaozz.zookeeper.config;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author HouYC
 * @create 2020-09-10-20:51
 */
public class WatchCallBack implements Watcher, AsyncCallback.StringCallback,
        AsyncCallback.Children2Callback, AsyncCallback.StatCallback {

    private static ZooKeeper zk;
    private String threadName;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private String pathName;

    /**
     * Watcher 监听事件
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {

        //如果第一个哥们，那个锁释放了，其实只有第二个收到了回调事件，
        //如果不是第一个哥们，其它一个挂了，也会通知后一个哥们回调事件，
        switch (watchedEvent.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "xxx");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
                default:
                    System.out.println("执行了默认");
        }
    }

    public void setZk(ZooKeeper zookeeper) {
        zk = zookeeper;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void tryLock() {
        System.out.println(threadName + "我创建了Zookeeper节点");

        try {
            // 创建有序的节点
            zk.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL, this, "cc");
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * StringCallBack 创建节点的回调
     * @param i
     * @param s
     * @param o
     * @param name
     */
    @Override
    public void processResult(int i, String s, Object o, String name) {

        //如果创建的节点不为null，则创建成功
        if (name != null) {
            System.out.println(threadName + "创建 节点：" + name);
            pathName = name;

            //获取/testLock 下所有的节点，不监听
            zk.getChildren("/", false, this, "abc");
        }
    }

    /**
     *  getChildren 回调
     * @param i
     * @param s
     * @param o
     * @param childrenList
     * @param stat
     */
    @Override
    public void processResult(int i, String s, Object o, List<String> childrenList, Stat stat) {

        //对这些节点拍个顺序
        Collections.sort(childrenList);

        //获取第一个节点线程
        int index = childrenList.indexOf(pathName.substring(1));

        if (index == 0) {
            //是第一个
            System.out.println(threadName + "是第一个。。。");
            try {

                zk.setData("/", threadName.getBytes(), -1);
                //阻塞
                countDownLatch.countDown();

            } catch (KeeperException e) {

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            //监听前一个节点
            zk.exists("/" + childrenList.get(i -1), this, this, "aaa");
        }

    }

    /**
     * exists 回调
     * @param i
     * @param s
     * @param o
     * @param stat
     */
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        System.out.println("......");
    }

    /**
     * 释放锁
     */
    public void unLock() {
        try {
            zk.delete(pathName, -1);
            System.out.println(threadName + "我释放锁了。。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
