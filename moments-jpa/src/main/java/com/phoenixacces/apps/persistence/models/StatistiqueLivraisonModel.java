package com.phoenixacces.apps.persistence.models;

import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livraisons;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livreurs;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class StatistiqueLivraisonModel implements Serializable {

    private double totalMtGlobal;

    private double totalMtLivraison;

    private double nbreClient;

    private double nbreLivreur;

    private double nbreLivraison;

    private double nbreLivraisonAffecte;

    private double nbreLivraisonAnnule;

    private double nbreLivraisonEffectue;

    private List<Livraisons> livraisonsList;

    private List<Livreurs> livreursList;

    private double nbreLivreurDispo;

    private double nbreLivreurNonDispo;

    private double nbreLivreurSuspendu;

    private List<Profile> profileList;
}
