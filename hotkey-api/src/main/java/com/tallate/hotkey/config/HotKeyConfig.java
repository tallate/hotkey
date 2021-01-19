package com.tallate.hotkey.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hot-key")
public class HotKeyConfig {

    /**
     * 当前服务器地址
     */
    private String currentServerAddr = "127.0.0.1";

    /**
     * 当前服务器暴露的HTTP端口
     */
    private int currentServerPort = 8080;

    /**
     * 热key服务器地址
     */
    private String hotKeyServerAddr = "http://127.0.0.1:8081";

    /**
     * 最多保留的热key数量
     */
    private int maxSize = 100;

    /**
     * 刷新间隔时间 / s
     */
    private int refreshInterval = 30;

}
