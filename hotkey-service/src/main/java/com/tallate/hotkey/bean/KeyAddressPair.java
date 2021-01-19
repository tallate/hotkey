package com.tallate.hotkey.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class KeyAddressPair implements Serializable {

    private String key;

    private String address;

}
