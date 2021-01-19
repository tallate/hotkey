package com.tallate.hotkey.detect;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectRequest implements Serializable {

    private List<String> hotKeys;

}
