package com.tallate.hotkey.upload;

import com.tallate.hotkey.APIResponse;
import com.tallate.hotkey.HotKeyItem;
import com.tallate.hotkey.HotKeyUploadData;
import com.tallate.hotkey.config.HotKeyConfig;
import com.tallate.hotkey.statistic.HotKeyStatistic;
import com.tallate.hotkey.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HotKeyUploader {

    private HotKeyConfig hotKeyConfig;

    private static final String UPLOAD_PATH = "/hot-key/upload";

    private ScheduledExecutorService task = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "hot-key-uploader"), new ThreadPoolExecutor.CallerRunsPolicy());

    public HotKeyUploader(HotKeyConfig hotKeyConfig) {
        this.hotKeyConfig = hotKeyConfig;
    }

    private String getCurrentServerAddress() {
        return hotKeyConfig.getCurrentServerAddr() + ":" + hotKeyConfig.getCurrentServerPort();
    }

    private Runnable uploader = () -> {
        List<HotKeyItem> items = HotKeyStatistic.getCurrentHotKeySet();
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        HotKeyUploadData hotKeyUploadData = new HotKeyUploadData();
        hotKeyUploadData.setItems(items);
        hotKeyUploadData.setAddress(getCurrentServerAddress());
        hotKeyUploadData.setCollectTime(new Date());
        log.info("[热点上报]执行任务开始, server:{}, uploadData:{}",
                hotKeyConfig.getHotKeyServerAddr() + UPLOAD_PATH, hotKeyUploadData);
        try {
            HttpUtil.post(hotKeyConfig.getHotKeyServerAddr() + UPLOAD_PATH,
                    hotKeyUploadData, APIResponse.class);
        } catch (Exception e) {
            log.warn("[热点上报]执行任务出错, uploadData:{}", hotKeyUploadData, e);
            return;
        }
        log.info("[热点上报]执行任务结束, uploadData:{}", hotKeyUploadData);
    };

    public void start() {
        log.info("[热点上报]提交上报任务");
        task.scheduleAtFixedRate(uploader, 3, 3, TimeUnit.SECONDS);
    }

}
