package com.tallate.hotkey;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HotKeyUploadData {

    private List<HotKeyItem> items;

    /**
     * 收集热点统计数据的时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat( pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date collectTime;

    /**
     * 上报客户端的IP:PORT
     */
    private String address;

}
