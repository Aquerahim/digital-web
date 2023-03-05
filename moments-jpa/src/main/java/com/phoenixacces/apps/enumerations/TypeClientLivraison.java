package com.phoenixacces.apps.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@ToString
public enum TypeClientLivraison implements Serializable {
    PHYSIQUE("PARTICULIER"),
    ONG("ONG"),
    ENTREPRISE("ENTREPRISE"),
    MORALE("ENTREPRISE");

    private final String label;
}
