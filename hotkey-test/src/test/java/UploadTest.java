import com.tallate.hotkey.APIResponse;
import com.tallate.hotkey.HotKeyUploadData;
import com.tallate.hotkey.util.HttpUtil;
import org.junit.Test;

/**
 * @author hgc
 * @date 1/17/21
 */
public class UploadTest {

    @Test
    public void testUpload() {
        HttpUtil.post("http://127.0.0.1:8081/hot-key/upload",
                new HotKeyUploadData(), APIResponse.class);
    }

}
