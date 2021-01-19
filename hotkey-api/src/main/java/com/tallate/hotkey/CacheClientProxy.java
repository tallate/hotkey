package com.tallate.hotkey;

import com.tallate.hotkey.config.HotKeyConfig;
import com.tallate.hotkey.detect.HotKeyDetectSet;
import com.tallate.hotkey.localcache.LocalCache;
import com.tallate.hotkey.statistic.HotKeyStatistic;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

@Slf4j
public class CacheClientProxy {

    private RedissonClient redisClient;

    private LocalCache localCache;

    public CacheClientProxy(RedissonClient redisClient, HotKeyConfig hotKeyConfig) {
        this.redisClient = redisClient;
        localCache = new LocalCache(redisClient, hotKeyConfig);

    }

    public void put(String key, String value) {
        RBucket<Object> bucket = redisClient.getBucket(key);
        bucket.set(value);
    }

    public String get(String key) {
        // 热点数据直接从本地缓存加载
        if (HotKeyDetectSet.isHot(key)) {
            log.info("检测到该key为热点key，从本地缓存获取数据, key:{}", key);
            String value = localCache.get(key);
            if (null != value) {
                HotKeyStatistic.incr(key);
            }
            return value;
        }
        RBucket bucket = redisClient.getBucket(key);
        Object value = bucket.get();
        // 如果缓存存在则加载到热点数据列表
        if (null != value) {
            HotKeyStatistic.incr(key);
        }
        return value == null ? "" : value.toString();
    }

}
