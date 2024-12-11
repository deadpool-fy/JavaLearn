package com.example.javalearn.work;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MyThread {

    public static void main(String[] args) {
        int corePoolSize = 5;//核心线程数
        int maximumPoolSize = 10;//最大线程数=核心线程数+救急线程数
        long keepAliveTime = 10;//救急线程的存活时间
        TimeUnit unit = TimeUnit.SECONDS; //救急线程存活时间的时间单位
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(10);//阻塞队列,当没有空闲的核心线程时,新来的线程会进入阻塞队列等待,如果阻塞队列满了会产生救急线程
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue,
                new CustomThreadFactory(),//线程工厂,可用于给线程起名
                new ThreadPoolExecutor.DiscardOldestPolicy()//拒绝策略,当超出最大线程数时,新来的线程的处理策略
        );

        // 提交一些任务
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            threadPoolExecutor.submit(() -> {
                System.out.println("执行任务：" + taskId + "，线程名：" + Thread.currentThread().getName());
                try {
                    // 模拟任务执行
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }


        threadPoolExecutor.shutdown();
    }
}

//自定义线程工厂
class CustomThreadFactory implements ThreadFactory {

    private static final String THREAD_NAME_PREFIX = "CustomThread-";  // 自定义线程前缀
    private static final AtomicInteger threadCount = new AtomicInteger(1);  // 线程编号
    private static final ThreadGroup group = Thread.currentThread().getThreadGroup();  // 获取当前线程组

    @Override
    public Thread newThread(Runnable r) {
        // 根据自定义规则创建线程，给线程命名
        Thread thread = new Thread(group, r, THREAD_NAME_PREFIX + threadCount.getAndIncrement());
        thread.setDaemon(false);  // 设置线程为非守护线程（默认）
        return thread;
    }
}