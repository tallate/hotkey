package com.tallate.hotkey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HotKeyItem implements Serializable {

    private String key;

    private int count;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HotKeyItem that = (HotKeyItem) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
