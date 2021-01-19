package com.tallate.test.config;

import com.tallate.hotkey.HotKeyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HotKeyConfig {

    @Bean
    public HotKeyConfigurer hotKeyConfigurer() {
        return new HotKeyConfigurer();
    }

}
