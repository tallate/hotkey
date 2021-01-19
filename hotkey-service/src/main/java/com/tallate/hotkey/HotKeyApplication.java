package com.tallate.hotkey;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.tallate.hotkey.dao")
@EnableScheduling
@SpringBootApplication
public class HotKeyApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotKeyApplication.class, args);
    }

}
