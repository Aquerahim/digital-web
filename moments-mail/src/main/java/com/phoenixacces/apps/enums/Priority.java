package com.phoenixacces.apps.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public enum Priority implements Serializable {
    LEVEL_1(0L, 1),
    LEVEL_2(1L, 2),
    LEVEL_3(2L, 3),
    LEVEL_4(3L, 4),
    LEVEL_5(4L, 5);

    private long id;
    private long level;
}
