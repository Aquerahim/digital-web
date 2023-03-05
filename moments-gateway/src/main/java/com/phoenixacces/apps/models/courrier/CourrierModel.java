package com.phoenixacces.apps.models.courrier;

import com.phoenixacces.apps.enumerations.TypeEnvoi;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.EntitesOrOE;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CourrierModel implements Serializable {
    private long jourNaissance;
    private String moisNaissance;
    private String nomExpediteur;
    private String phoneExpedietur;
    private TypeEnvoi typeEnvoi;
    private String adresseExpediteur;
    private String villeExpeditrice;
    private String nomDestinataire;
    private String phoneDestinatire;
    private String villeDestinatrice;
    private String qtionFrais;
    private String frais;
    private double fraisEnvoi;
    private double valeurColis;
    private EntitesOrOE gare;
    private Profile profile;
    private List<ColisModel> colis;
}
