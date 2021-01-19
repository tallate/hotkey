package com.tallate.hotkey.controller;

import com.google.common.base.Preconditions;
import com.tallate.hotkey.APIResponse;
import com.tallate.hotkey.HotKeyUploadData;
import com.tallate.hotkey.service.HotKeyService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/hot-key")
public class HotKeyController {

    @Resource
    private HotKeyService hotKeyService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public APIResponse upload(@RequestBody HotKeyUploadData data) {
        Preconditions.checkNotNull(data, "数据不能为空");
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(data.getItems()), "热点数据不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(data.getAddress()), "客户端地址不能为空");
        hotKeyService.save(data);
        return APIResponse.success();
    }

}
