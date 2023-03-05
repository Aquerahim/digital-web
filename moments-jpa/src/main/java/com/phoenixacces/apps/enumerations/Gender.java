package com.phoenixacces.apps.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@ToString
public enum Gender implements Serializable {
    MASCULIN("MASCULIN"),
    FEMININ("FEMININ"),
    DEFAULT("....");

    private final String key;
}
