package com.tallate.hotkey.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.tallate.hotkey.APIResponse;
import com.tallate.hotkey.HotKey;
import com.tallate.hotkey.HotKeyItem;
import com.tallate.hotkey.HotKeyUploadData;
import com.tallate.hotkey.bean.KeyAddressPair;
import com.tallate.hotkey.dao.HotKeyDao;
import com.tallate.hotkey.detect.DetectRequest;
import com.tallate.hotkey.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HotKeyService {

    private static final String DETECT_PATH = "/hot-key/notify";

    @Resource
    private HotKeyDao hotKeyDao;

    /**
     * a到b还有多少秒
     */
    private long timeDiff(Date a, Date b) {
        return (b.getTime() - a.getTime()) / 1000;
    }

    private BigDecimal getRate(int countDiff, long timeDiff) {
        return new BigDecimal((double) countDiff / timeDiff);
    }

    private void saveHotKey2DB(Date collectTime, String address, HotKeyItem item) {
        HotKey param = HotKey.builder()
                .key(item.getKey())
                .address(address)
                .build();
        Page<HotKey> page = PageHelper.startPage(1, 1);
        List<HotKey> list = hotKeyDao.queryRecentCollects(param);
        if (CollectionUtils.isEmpty(list)) {
            hotKeyDao.save(HotKey.builder()
                    .key(item.getKey())
                    .count(item.getCount())
                    .address(address)
                    .collectTime(collectTime)
                    .rate(BigDecimal.ZERO)
                    .build());
            return;
        }
        HotKey last = list.get(0);
        long t = timeDiff(last.getCollectTime(), collectTime);
        if (t <= 0) {
            // 同一条采集，直接忽略
            return;
        }
        int countDiff = item.getCount() - last.getCount();
        if (countDiff <= 0) {
            // 可能是重启了开始重新采集
            hotKeyDao.save(HotKey.builder()
                    .key(item.getKey())
                    .count(item.getCount())
                    .address(address)
                    .collectTime(collectTime)
                    .rate(BigDecimal.ZERO)
                    .build());
            return;
        }
        hotKeyDao.save(HotKey.builder()
                .key(item.getKey())
                .count(item.getCount())
                .address(address)
                .collectTime(collectTime)
                .rate(getRate(countDiff, t))
                .build());
    }

    /**
     * 避免上报的地址有误
     */
    private String fixAddress(String origin) {
        if (StringUtils.isBlank(origin)) {
            return origin;
        }
        if (!origin.startsWith("http")) {
            return "http://" + origin;
        }
        return origin;
    }

    public void save(HotKeyUploadData data) {
        data.getItems().stream()
                // 同一个key只存一次
                .distinct()
                .forEach(item -> saveHotKey2DB(data.getCollectTime(), fixAddress(data.getAddress()), item));
    }

    private Date getLastSampleTime(int n) {
        Page<HotKey> page = PageHelper.startPage(1, n);
        List<HotKey> hotKeys = hotKeyDao.queryRecentCollects(HotKey.builder().build());
        if (CollectionUtils.isEmpty(hotKeys)) {
            return null;
        }
        HotKey hotKey = hotKeys.stream().min(Comparator.comparing(HotKey::getCollectTime))
                .orElse(null);
        return null == hotKey ? null : hotKey.getCollectTime();
    }

    /**
     * @return <address, keyList>
     */
    private Map<String, List<String>> getHotKeys(Date lastSampleTime, int n) {
        // 分页查询哪些key属于热key
        Page<Object> page = PageHelper.startPage(1, n);
        List<HotKey> hotKeys = hotKeyDao.queryHotKeys(lastSampleTime);
        log.debug("热点数据统计: {}", hotKeys);
        if (CollectionUtils.isEmpty(hotKeys)) {
            return Maps.newHashMap();
        }
        // 找出上报这些key的服务器
        List<String> keys = hotKeys.stream()
                .map(HotKey::getKey)
                .collect(Collectors.toList());
        List<KeyAddressPair> keyAddressPairs = hotKeyDao.queryAllAddress(keys);
        // 按服务器地址聚合
        return keyAddressPairs.stream()
                .collect(Collectors.toMap(KeyAddressPair::getAddress,
                        p -> Lists.newArrayList(p.getKey()),
                        (l1, l2) -> {
                            l1.addAll(l2);
                            return l1;
                        }));
    }

    private Date minusSeconds(Date d, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.SECOND, -seconds);
        return calendar.getTime();
    }

    public Map<String, List<String>> queryByRate(int n, int collectTime) {
        // 先查询最后一次采样的时间
        Date lastSampleTime = getLastSampleTime(n);
        if (null == lastSampleTime) {
            return Maps.newHashMap();
        }
        // 最后一次采样时间往前推采样间隔时间
        lastSampleTime = minusSeconds(lastSampleTime, collectTime);
        // 找该次采样结果中频率最高的
        return getHotKeys(lastSampleTime, n);
    }

    public void dispatch(Map<String, List<String>> addrKeysMap) {
        addrKeysMap.forEach((addr, keys) -> {
            DetectRequest detectRequest = DetectRequest.builder()
                    .hotKeys(keys)
                    .build();
            String url = addr + DETECT_PATH;
            try {
                APIResponse response = HttpUtil.post(url, detectRequest, APIResponse.class);
                if (response.isSuccess()) {
                    log.info("url[{}], 通知完毕, keys:{}", url, keys);
                } else {
                    log.info("url[{}], 通知失败, keys:{}", url, keys);
                }
            } catch (Exception e) {
                log.info("url[{}], 通知出错, keys:{}", url, keys, e);
            }
        });
    }

}
