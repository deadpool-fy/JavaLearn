package com.example.javalearn.work;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class RedisDistributedLock {

    private final Jedis jedis;
    private final String lockKey = "lock_key";  // 锁的key
    private final int lockTimeout = 30;  // 锁的超时时间（秒）

    private final SetParams params = new SetParams();

    public RedisDistributedLock(Jedis jedis) {
        this.jedis = jedis;
    }

    // 获取分布式锁
    public boolean acquireLock(String lockValue) {
        params.ex(lockTimeout);//设置key的过期时间,单位为秒  px也是设置key的过期时间，单位为毫秒
        params.nx();           //只有在键不存在时设置,  xx为只有在键存在时设置
        // 使用SETNX命令，尝试设置锁的值
        String result = jedis.set(lockKey, lockValue, params);
        return "OK".equals(result);
    }

    // 释放分布式锁
    public boolean releaseLock(String lockValue) {
        // 获取锁的值
        String currentValue = jedis.get(lockKey);

        // 判断是否是当前线程获得的锁，避免误删
        if (lockValue.equals(currentValue)) {
            jedis.del(lockKey);
            return true;
        }
        return false;
    }
}
