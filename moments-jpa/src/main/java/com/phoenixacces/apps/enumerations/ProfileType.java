package com.phoenixacces.apps.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@ToString
public enum ProfileType implements Serializable {
    CEO("CEO"),
    COO("COO"),
    MANAGER("RESPONSABLE POINT SERVICE OU RELAIS"),
    COMPTABILITE( "COMPTABILITE"),
    EXPERT("ADMIN PHOENIX ACCES"),
    ADMIN("ADMINISTRATEUR LOCAL"),
    ADMIN_PART("ADMINISTRATEUR PARTENAIRE"),
    INFORMATIQUE("INFORMATIQUE"),
    GESTIONNAIRE("GESTIONNAIRE"),
    COURRIER("CHARGE CLIENTEL - SERVICE COURRIER"),
    TICKETING("CHARGE CLIENTEL - SERVICE VOYAGE"),
    PARTENAIRE("PARTENAIRE"),
    LIVREUR("LIVREUR"),
    RESPO_PART("RESPONSABLE STRUCTURE PARTENAIRE");

    private final String label;
}
