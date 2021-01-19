package com.tallate.hotkey;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HotKey implements Serializable {

    private int id;

    private String key;

    private int count;

    private Date collectTime;

    private String address;

    private BigDecimal rate;

}
