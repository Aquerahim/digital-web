package com.phoenixacces.apps.models.courrier;

import com.phoenixacces.apps.enumerations.TypeEnvoi;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.Colis;
import com.phoenixacces.apps.persistence.entities.parametrage.EntitesOrOE;
import com.phoenixacces.apps.persistence.entities.parametrage.SuiviDemande;
import com.phoenixacces.apps.persistence.entities.service.SmsEnvoye;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CourrierMapping implements Serializable {
    private int ordre;
    private int find;
    private String refColis;
    private String nomExpediteur;
    private String phoneExpedietur;
    private TypeEnvoi typeEnvoi;
    private String adresseExpediteur;
    private String villeExpeditrice;
    private String nomDestinataire;
    private String phoneDestinatire;
    private String villeDestinatrice;
    private String frais;
    private double montantFrais;
    private double valeurColis;
    private double totalPaye;
    private double suiviSms;
    private EntitesOrOE gare;
    private Profile profile;
    private SuiviDemande suivi;
    private List<SmsEnvoye> smsEnvoyes;
    private String birthDay;
    private List<Colis> colisList;
    private Instant creation;
    private String valideur;
}
