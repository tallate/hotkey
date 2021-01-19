package com.tallate.test.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spring.redisson")
@Configuration
public class RedissonConfig extends CachingConfigurerSupport {

    private String address;

    @Bean("redisson")
    RedissonClient redisson() throws Exception {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(address)
                .setKeepAlive(true)
                .setPingConnectionInterval(3000);
        return Redisson.create(config);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
