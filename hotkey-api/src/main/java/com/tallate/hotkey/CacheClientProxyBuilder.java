package com.tallate.hotkey;

import com.tallate.hotkey.config.HotKeyConfig;
import org.redisson.api.RedissonClient;

/**
 * Redis客户端代理
 * 1、如果是热key，取本地缓存中的值
 * 当然，这时Redis是旁路缓存，本地缓存不存在的时候是要回源到Redis的
 * 本地缓存使用Guava，本身包含了LRU、虚引用的特性
 * 2、不是热key，仍然从Redis读数据
 */
public class CacheClientProxyBuilder {

    private HotKeyConfig hotKeyConfig;

    public CacheClientProxyBuilder(HotKeyConfig hotKeyConfig) {
        this.hotKeyConfig = hotKeyConfig;
    }

    public CacheClientProxy build(RedissonClient redissonClient) {
        return new CacheClientProxy(redissonClient, hotKeyConfig);
    }

}
