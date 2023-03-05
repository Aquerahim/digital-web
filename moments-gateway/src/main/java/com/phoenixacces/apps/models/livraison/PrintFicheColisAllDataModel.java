package com.phoenixacces.apps.models.livraison;

import com.phoenixacces.apps.persistence.entities.module.livraison.FicheBonDeCommande;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PrintFicheColisAllDataModel implements Serializable {
    private String exemplaire;
    private String auteur;
    private String logoPhoenix;
    private String logoEntreprise;
    private String type;
    private Long idEntreprise;
    private double nbrTraite;
    private double recupere;
    private double pending;
    private double total;
    private List<FicheBonDeCommande> list;
}
