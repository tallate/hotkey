package com.tallate.hotkey.util;

import lombok.Data;

import java.util.Objects;

/**
 * P相等就认为相等
 */
@Data
public class Pair<P, V> {

    private P p;

    private V v;

    public Pair(P p, V v) {
        this.p = p;
        this.v = v;
    }

    public static <P, V> Pair<P, V> of(P p, V v) {
        return new Pair<>(p, v);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(p, pair.p);
    }

    @Override
    public int hashCode() {
        return Objects.hash(p);
    }
}
