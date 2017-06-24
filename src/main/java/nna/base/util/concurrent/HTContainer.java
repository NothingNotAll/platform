package nna.base.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * high throughput; VHT; high-throughput;
 高性能市场数据分发：提供高吞吐量和路由功能。
 * @author NNA-SHUAI
 * @create 2017-06-24 17:29
 **/

public class HTContainer<T> {
    private AtomicInteger takeIndex=new AtomicInteger();
    private AtomicInteger addIndex=new AtomicInteger();
    private Node head;
    private Node tail;
    private class Node{
        private Node prev;
        private Node next;
    }
}
