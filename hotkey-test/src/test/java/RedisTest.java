import com.tallate.hotkey.CacheClientProxy;
import com.tallate.hotkey.CacheClientProxyBuilder;
import com.tallate.hotkey.statistic.HotKeyStatistic;
import com.tallate.test.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TestApplication.class)
public class RedisTest {

    @Resource
    private CacheClientProxyBuilder cacheClientProxyBuilder;

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void testSetGet() throws InterruptedException {
        CacheClientProxy cacheClientProxy = cacheClientProxyBuilder.build(redissonClient);
        cacheClientProxy.put("hello", "hello");
        Object value = cacheClientProxy.get("hello");
        // == hello
        System.out.println(value);
        // 只有统计到hello一个key
        System.out.println(HotKeyStatistic.getCurrentHotKeySet());
        // 等待热点上报
        Thread.sleep(20000);
    }

}
