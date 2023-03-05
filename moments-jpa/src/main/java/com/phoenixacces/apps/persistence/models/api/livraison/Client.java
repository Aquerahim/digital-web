package com.phoenixacces.apps.persistence.models.api.livraison;

import lombok.Data;

import java.io.Serializable;

@Data
public class Client implements Serializable {
    private String nom;
    private String prenom;
    private String contact;
}
