package com.tallate.hotkey.statistic;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tallate.hotkey.HotKeyItem;
import com.tallate.hotkey.config.HotKeyConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class HotKeyStatistic {

    private static final int STATISTIC_CAPACITY = 100;

    /**
     * 数量限制，只保留指定数量的元素
     * 最小堆，即频率最低的在队头，每次将新元素与堆头的比较，留下其中较大者
     */
    private static PriorityQueue<HotKeyItem> priorityQueue =
            new PriorityQueue<>(STATISTIC_CAPACITY,
                    Comparator.comparingInt(HotKeyItem::getCount));

    private static Set<String> existedSet = Sets.newHashSet();

    private static final ReentrantLock LOCK = new ReentrantLock();

    private static LoadingCache<String, LongAdder> counters;

    public static void init(HotKeyConfig hotKeyConfig) {
        counters = CacheBuilder.newBuilder()
                .maximumSize(hotKeyConfig.getMaxSize())
                // 注意过了这个时间后计数器会重置，此时原来的热点数据会被清掉
                .refreshAfterWrite(hotKeyConfig.getRefreshInterval(), TimeUnit.SECONDS)
                .build(new CacheLoader<String, LongAdder>() {
                    @Override
                    public LongAdder load(String key) {
                        return new LongAdder();
                    }
                });
    }

    public static void incr(String key) {
        LongAdder data;
        try {
            data = counters.get(key);
        } catch (ExecutionException e) {
            log.warn("获取热key统计缓存出错", e);
            return;
        }
        // 并发安全
        LOCK.lock();
        try {
            data.increment();
            int count = data.intValue();
            HotKeyItem item = new HotKeyItem(key, count);
            // 之前未收集到任何热key
            if (priorityQueue.isEmpty()) {
                log.info("之前未收集到任何热点数据, key:{}, count:{}", key, count);
                priorityQueue.offer(item);
                existedSet.add(key);
            }
            // 之前收集过该key，则肯定比堆头的大，因此直接重新插入一次
            else if (existedSet.contains(key)) {
                log.info("之前已收集到热点数据，重新插入, key:{}, count:{}", key, count);
                priorityQueue.remove(item);
                priorityQueue.offer(item);
            }
            // 如果之前收集的少于阈值个数
            else if (existedSet.size() < STATISTIC_CAPACITY) {
                log.info("之前收集的个数少于阈值, key:{}, count:{}", key, count);
                priorityQueue.offer(item);
                existedSet.add(key);
            }
            // 和堆头部比较，如果比堆头小则忽略，否则替换
            else {
                HotKeyItem head = priorityQueue.peek();
                if (item.getCount() > head.getCount()) {
                    log.info("比之前收集的最小值要大，替换之, key:{}, count:{}", key, count);
                    priorityQueue.poll();
                    existedSet.remove(head.getKey());
                    priorityQueue.offer(item);
                    existedSet.add(key);
                }
            }
        } finally {
            LOCK.unlock();
        }
    }

    @SuppressWarnings("all")
    public static List<HotKeyItem> getCurrentHotKeySet() {
        HotKeyItem[] items = priorityQueue.toArray(new HotKeyItem[0]);
        return Lists.newArrayList(items);
    }

}
