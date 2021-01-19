package com.tallate.hotkey.task;

import com.tallate.hotkey.service.HotKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HotKeyNotifyTask {

    @Value("${hotkey.sample.collectTime}")
    private int sampleCollectTime;

    @Value("${hotkey.sample.count}")
    private int sampleCount;

    @Resource
    private HotKeyService hotKeyService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void schedule() {
        log.info("开始统计热点数据");
        Map<String, List<String>> addrKeysMap = hotKeyService.queryByRate(sampleCount, sampleCollectTime);
        hotKeyService.dispatch(addrKeysMap);
        log.info("结束统计热点数据");
    }


}
