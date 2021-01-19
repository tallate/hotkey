package com.tallate.hotkey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse<T> {

    private int resCode;

    private String msg;

    private T t;

    public APIResponse(int resCode, String msg) {
        this(resCode, msg, null);
    }

    public boolean isSuccess() {
        return resCode == 0;
    }

    public static <T> APIResponse<T> success() {
        return new APIResponse<>(0, "", null);
    }

    public static <T> APIResponse<T> success(T t) {
        return new APIResponse<>(0, "", t);
    }

    public static APIResponse error(int resCode, String msg) {
        return new APIResponse(resCode, msg);
    }

}
