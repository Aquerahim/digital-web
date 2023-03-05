package com.phoenixacces.apps.persistence.entities.module;

import com.phoenixacces.apps.enumerations.TypeEnvoi;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.EntitesOrOE;
import com.phoenixacces.apps.persistence.entities.parametrage.SuiviDemande;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_service_courrier", schema = "digitalweb_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ServiceCourrier implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NonNull
    @Column(name = "ordre", length = 10)
    private int ordre;


    @NonNull
    @Column(name = "valideur", length = 15)
    private String valideur;


    @NonNull
    @Column(name = "colis_number", length = 15)
    private String colisNumber;


    @NonNull
    @Column(name = "nomExpediteur")
    private String nomExpediteur;


    @NonNull
    @Column(name = "phoneExpedietur")
    private String phoneExpedietur;


    @NonNull
    @Column(name = "typeEnvoi")
    private TypeEnvoi typeEnvoi;


    @NonNull
    @Column(name = "adresseExpediteur")
    private String adresseExpediteur;


    @NonNull
    @Column(name = "villeExpeditrice")
    private String villeExpeditrice;


    @NonNull
    @Column(name = "nomDestinataire")
    private String nomDestinataire;


    @NonNull
    @Column(name = "phoneDestinatire")
    private String phoneDestinatire;


    @NonNull
    @Column(name = "villeDestinatrice")
    private String villeDestinatrice;


    @NonNull
    @Column(name = "frais")
    private String frais;


    @NonNull
    @Column(name = "monatFrais")
    private double monatFrais;


    @NonNull
    @Column(name = "valeurColis")
    private double valeurColis;


    @ManyToOne
    @JoinColumn(name = "gare_id", foreignKey = @ForeignKey(name = "fk__service_courrier__gare_id"))
    private EntitesOrOE gare;


    @ManyToOne
    @JoinColumn(name = "Suivi_demande_id", foreignKey = @ForeignKey(name = "fk__service_courrier__Suivi_demande_id"))
    private SuiviDemande suiviDemande;


    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__service_courrier__profile_id"))
    private Profile profile;


    @Column(name="active")
    private boolean active;


    @Column(name="retrait")
    private boolean retrait;


    @Column(name="version", length = 1)
    private int version;


    private Instant creation;


    private Instant lastUpdate;

    @NonNull
    @Column(name = "montantSuiviSMS")
    private double montantSuiviSMS;

    @NonNull
    @Column(name = "montantTotalPayer")
    private double montantTotalPayer;

    @Transient
    @ManyToOne
    @JoinColumn(name = "colis_id", foreignKey = @ForeignKey(name = "fk__service_courrier__colis_id"))
    private Colis colis;
}
