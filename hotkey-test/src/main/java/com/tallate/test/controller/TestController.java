package com.tallate.test.controller;

import com.tallate.hotkey.CacheClientProxy;
import com.tallate.hotkey.CacheClientProxyBuilder;
import com.tallate.hotkey.detect.HotKeyDetectSet;
import com.tallate.hotkey.statistic.HotKeyStatistic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private CacheClientProxyBuilder cacheClientProxyBuilder;

    @Autowired
    private RedissonClient redissonClient;

    @RequestMapping("/isHot")
    public boolean isHot(@RequestParam String key) {
        return HotKeyDetectSet.isHot(key);
    }

    @RequestMapping("/getset")
    public void testSetGet() throws InterruptedException {
        CacheClientProxy cacheClientProxy = cacheClientProxyBuilder.build(redissonClient);
        cacheClientProxy.put("hello", "hello");
        Object value = cacheClientProxy.get("hello");
        // == hello
        System.out.println(value);
        // 只有统计到hello一个key
        System.out.println(HotKeyStatistic.getCurrentHotKeySet());
    }

    @RequestMapping("/getHotKeySet")
    public List<String> getHotKeySet() {
        return HotKeyDetectSet.getHotKeySet().entrySet().stream()
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }

}
