package com.tallate.hotkey.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConcurrentSet<T> extends ConcurrentHashMap<T, Object> {

    private static final Object NULL_VALUE = new Object();

    public void add(T t) {
        put(t, NULL_VALUE);
    }

    public void addAll(List<T> list) {
        if (null == list) {
            return;
        }
        putAll(list.stream().collect(Collectors.toMap(i -> i, i -> NULL_VALUE)));
    }

    @Override
    public boolean contains(Object t) {
        return containsKey(t);
    }

}
