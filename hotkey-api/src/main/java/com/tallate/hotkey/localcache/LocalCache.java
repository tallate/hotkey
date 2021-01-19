package com.tallate.hotkey.localcache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tallate.hotkey.config.HotKeyConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalCache {

    private LoadingCache<String, Optional<String>> cache;

    public LocalCache(RedissonClient redissonClient, HotKeyConfig hotKeyConfig) {
        cache = CacheBuilder.newBuilder()
                // 热key可以保存的数量
                .maximumSize(hotKeyConfig.getMaxSize())
                // 过期时间要比热key探测时间间隔长
                .refreshAfterWrite(hotKeyConfig.getRefreshInterval(), TimeUnit.SECONDS)
                .build(new CacheLoader<String, Optional<String>>() {
                    @Override
                    public Optional<String> load(String key) throws Exception {
                        RBucket<String> bucket = redissonClient.getBucket(key);
                        String result = bucket.get();
                        // 直接返回null的话LoadingCache会报错
                        return null == result ? Optional.empty() : Optional.of(result);
                    }
                });
    }

    public String get(String key) {
        try {
            return cache.get(key).orElse(null);
        } catch (ExecutionException e) {
            log.warn("获取出错", e);
            return null;
        }
    }

}
