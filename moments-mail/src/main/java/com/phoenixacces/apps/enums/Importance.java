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
public enum Importance implements Serializable {
    LOW(0L, "LOW"),
    NORMAL(1L, "NORMAL"),
    HIGH(2L, "HIGH");

    private long id;
    private String label;
}
