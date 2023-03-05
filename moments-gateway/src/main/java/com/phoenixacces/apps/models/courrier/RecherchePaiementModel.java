package com.phoenixacces.apps.models.courrier;

import lombok.*;

import java.io.Serializable;

@Data
public class RecherchePaiementModel implements Serializable {
    private String refPaiement;
    private String numPayeur;
}
