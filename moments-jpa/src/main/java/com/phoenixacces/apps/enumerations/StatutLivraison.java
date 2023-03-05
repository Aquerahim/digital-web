package com.phoenixacces.apps.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@ToString
public enum StatutLivraison  implements Serializable {
    LIVRE("LIVRAISON EFFECTUÉE"),
    NON_LIVRE("NON-LIVRE"),
    EN_ATTENTE("EN ATTENTE DE LIVRAISON"),
    EN_COURS("EN COURS D'ARCHEMINEMENT"),
    ANNULER("LIVRAISON ANNULÉE"),
    REFUS("REFUS"),
    ENVOI("MESSAGE ENVOYÉ"),

    PENDING("MESSAGE EN ATTENTE"),
    NON_CASE("LIVRAISON PAYÉ MAIS COLIS NON PRIS PAR LE CLIENT");

    private final String key;
}