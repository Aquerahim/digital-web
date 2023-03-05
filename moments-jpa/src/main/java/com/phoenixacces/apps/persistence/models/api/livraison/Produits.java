package com.phoenixacces.apps.persistence.models.api.livraison;

import lombok.Data;

import java.io.Serializable;

@Data
public class Produits implements Serializable {
    private String id;
    private String quantite;
    private String nom;
    private String typeProduit;
}
