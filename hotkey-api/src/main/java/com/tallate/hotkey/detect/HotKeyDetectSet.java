package com.tallate.hotkey.detect;

import com.tallate.hotkey.util.ConcurrentSet;

import java.util.List;

public class HotKeyDetectSet {

    private static final ConcurrentSet<String> DETECTED_HOT_KEY = new ConcurrentSet<>();

    public static void set(List<String> hotKeys) {
        synchronized (DETECTED_HOT_KEY) {
            DETECTED_HOT_KEY.clear();
            DETECTED_HOT_KEY.addAll(hotKeys);
        }
    }

    public static boolean isHot(String key) {
        return DETECTED_HOT_KEY.contains(key);
    }

    public static ConcurrentSet<String> getHotKeySet() {
        return DETECTED_HOT_KEY;
    }

}
