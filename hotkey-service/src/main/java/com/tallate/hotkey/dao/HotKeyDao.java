package com.tallate.hotkey.dao;

import com.tallate.hotkey.HotKey;
import com.tallate.hotkey.bean.KeyAddressPair;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HotKeyDao {

    void save(HotKey hotKey);

    List<KeyAddressPair> queryAllAddress(@Param("keys") List<String> keys);

    List<HotKey> queryRecentCollects(HotKey hotKey);

    List<HotKey> queryHotKeys(Date lastSampleTime);
}
