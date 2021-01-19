package com.tallate.hotkey;

import com.tallate.hotkey.config.HotKeyConfig;
import com.tallate.hotkey.detect.HotKeyDetectController;
import com.tallate.hotkey.statistic.HotKeyStatistic;
import com.tallate.hotkey.upload.HotKeyUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Configuration
@EnableConfigurationProperties(HotKeyConfig.class)
@ConditionalOnProperty(
        // demo.open字段如果为true的情况下就打开配置
        prefix = "hot-key",
        name = "open",
        havingValue = "true"
)
public class HotKeyConfigurer implements ApplicationContextAware, InitializingBean, ApplicationListener<WebServerInitializedEvent> {

    private ApplicationContext applicationContext;

    @Autowired
    private HotKeyConfig hotKeyConfig;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        // 初始化服务器地址
        try {
            hotKeyConfig.setCurrentServerAddr(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            log.info("获取当前服务器地址失败了，默认取127.0.0.1", e);
        }
        hotKeyConfig.setCurrentServerPort(event.getWebServer().getPort());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void initConfig() {
        hotKeyConfig.setMaxSize(hotKeyConfig.getMaxSize());
        hotKeyConfig.setRefreshInterval(hotKeyConfig.getRefreshInterval());
        hotKeyConfig.setCurrentServerAddr(hotKeyConfig.getCurrentServerAddr());
        hotKeyConfig.setCurrentServerPort(hotKeyConfig.getCurrentServerPort());
        hotKeyConfig.setHotKeyServerAddr(hotKeyConfig.getHotKeyServerAddr());
    }

    /**
     * 用于上报key访问统计
     */
    @Bean(initMethod = "start")
    public HotKeyUploader hotKeyUploader() {
        initConfig();
        return new HotKeyUploader(hotKeyConfig);
    }

    @Bean
    public HotKeyDetectController hotKeyDetectController() {
        return new HotKeyDetectController();
    }

    @Bean
    public CacheClientProxyBuilder cacheClientProxyBuilder() {
        initConfig();
        return new CacheClientProxyBuilder(hotKeyConfig);
    }

    /**
     * 等到其他bean都加载完后再加载
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        initConfig();
        // 用于统计热点数据
        HotKeyStatistic.init(hotKeyConfig);
    }

    private void registerBean(String beanName, Object bean) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        beanFactory.registerSingleton(beanName, bean);
    }

    private void unregisterBean(String beanName) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        beanFactory.removeBeanDefinition(beanName);
    }

    private RequestMappingHandlerMapping getHandlerMappingFromBeanFactory() {
        try {
            // 同时指定beanName和beanType，防止该类被框架重复注入
            return applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        } catch (NoSuchBeanDefinitionException e) {
            log.warn("未找到requestMappingHandlerMapping", e);
            return null;
        }
    }

    /**
     * 去掉Controller的Mapping
     */
    private void unregisterController(String controllerBeanName) {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = getHandlerMappingFromBeanFactory();
        if (requestMappingHandlerMapping != null) {
            Object controller = applicationContext.getBean(controllerBeanName);
            if (controller == null) {
                return;
            }
            final Class<?> targetClass = controller.getClass();
            ReflectionUtils.doWithMethods(targetClass, method -> {
                Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
                try {
                    Method createMappingMethod = RequestMappingHandlerMapping.class.
                            getDeclaredMethod("getMappingForMethod", Method.class, Class.class);
                    createMappingMethod.setAccessible(true);
                    RequestMappingInfo requestMappingInfo = (RequestMappingInfo)
                            createMappingMethod.invoke(requestMappingHandlerMapping, specificMethod, targetClass);
                    if (requestMappingInfo != null) {
                        requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                    }
                } catch (Exception e) {
                    log.info("注册Controller失败", e);
                }
            }, ReflectionUtils.USER_DECLARED_METHODS);
        }
    }

    private void registerController(String controllerBeanName) throws Exception {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = getHandlerMappingFromBeanFactory();
        if (requestMappingHandlerMapping != null) {
            Object controller = applicationContext.getBean(controllerBeanName);
            if (controller == null) {
                return;
            }
            // 先摘掉再注册，避免重复注册
            unregisterController(controllerBeanName);
            // 注册Controller
            Method method = requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().
                    getDeclaredMethod("detectHandlerMethods", Object.class);
            method.setAccessible(true);
            method.invoke(requestMappingHandlerMapping, controllerBeanName);
        }
    }

}
