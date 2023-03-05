package com.phoenixacces.apps.models.livraison;

import com.phoenixacces.apps.persistence.entities.module.livraison.BonDeCommande;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PrintFicheColisDayModel implements Serializable {
    private String etat;
    private String logoPhoenix;
    private String logoEntreprise;
    private String arrierePlan;
    private String exemplaire;
    private String nomPartenaire;
    private String nomEntreprise;
    private String montantLettre;
    private String abbrev;
    private String compagnie;
    private String contact;
    private String contactRespEntrep;
    private String adresse;
    private String email;
    private String rccm;
    private double montantGlobal;
    private double montantLivraisn;
    private String contactResponsable;
    private String activite;
    private String contactPart;
    private String zoneCouvPart;
    private String nomResponsable;
    private String ordre;
    private String reference;
    private String typeLivraison;
    private String datelivraisonSouhaite;
    private String zoneRecuperation;
    private String typeZoneRecup;
    private String precisionZoneRecup;
    private String auteur;
    private List<BonDeCommande> articles;
}
