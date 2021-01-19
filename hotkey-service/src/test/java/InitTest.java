import com.tallate.hotkey.HotKey;
import com.tallate.hotkey.HotKeyApplication;
import com.tallate.hotkey.dao.HotKeyDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = HotKeyApplication.class)
public class InitTest {

    @Resource
    private HotKeyDao hotKeyDao;

    @Test
    public void batchInsertTestData() throws InterruptedException {
        LongAdder i = new LongAdder();
        int size = 100000;
        int threadCount = 20;
        ExecutorService tp = new ThreadPoolExecutor(threadCount, threadCount, 1000, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        Runnable task = () -> {
            while (true) {
                int cur;
                if ((cur = i.intValue()) > size) {
                    return;
                }
                i.increment();
                HotKey hotKey = HotKey.builder()
                        .key(i + "")
                        .address("http://127.0.0.1:8080")
                        .collectTime(new Date())
                        .count(size - cur + 1)
                        .rate(new BigDecimal(1.0 * (size - cur + 1) / 10000))
                        .build();
                hotKeyDao.save(hotKey);
            }
        };
        for (int j = 0; j < threadCount; j++) {
            tp.execute(task);
        }
        tp.shutdown();
        tp.awaitTermination(10, TimeUnit.MINUTES);
    }


}

