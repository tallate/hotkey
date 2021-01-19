package com.tallate.hotkey.detect;

import com.tallate.hotkey.APIResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hot-key")
public class HotKeyDetectController {

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public APIResponse notifyHotKey(@RequestBody DetectRequest detectRequest) {
        if (detectRequest == null || detectRequest.getHotKeys() == null) {
            return APIResponse.error(1, "入参不能为空");
        }
        HotKeyDetectSet.set(detectRequest.getHotKeys());
        return APIResponse.success();
    }

}
