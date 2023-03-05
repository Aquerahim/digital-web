package com.phoenixacces.apps.persistence.entities.parametrage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.enumerations.StatutLivraison;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_rechargement_compte_sms", schema = "digitalweb_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk__ref_paiement", columnNames = {"ref_paiement"})
})
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "RechargementCompteSms", description = "Modele of Rechargement Compte Sms")
public class RechargementCompteSms implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "ordre")
    private String ordre;


    @Column(name = "nbre_sms")
    private int nbreSms;

    @NonNull
    @Column(name = "ref_paiement")
    private String refPaiement;

    @NonNull
    @Column(name = "num_payeur")
    private String numPayeur;

    @NonNull
    @Column(name = "type_formule")
    private String typeFormule;

    @NonNull
    @Column(name = "nom_formule")
    private String nomFormule;

    @NonNull
    @Column(name = "montant_rechargement")
    private Double montantRechargement;

    @NonNull
    @Column(name = "taxe_applique")
    private Double taxeApplique;


    @NonNull
    @Column(name = "total_payer")
    private Double totalPayer;


    @ManyToOne
    @JoinColumn(name = "type_paiement_id", foreignKey = @ForeignKey(name = "fk__rechargement_compte_sms__type_paiement_id"))
    private TypePaiement typePaiement;


    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__rechargement_compte_sms__profile_id"))
    private Profile profile;


    @ManyToOne
    @JoinColumn(name = "entreprises_id", foreignKey = @ForeignKey(name = "fk__rechargement_compte_sms__entreprises_id"))
    private Entreprises entreprises;


    @NonNull
    @Column(name = "statut")
    private EtatLecture statut;

    @ManyToOne
    @JoinColumn(name = "motif_id", foreignKey = @ForeignKey(name = "fk__rechargement_compte_sms__motif_id"))
    private MotifSuspensionCollaboration motif;


    @JsonIgnore
    @NonNull
    private Boolean active;

    @JsonIgnore
    private Instant creation;

    @JsonIgnore
    private Instant lastUpdate;


    @Transient
    private OffreSmsEntreprise offreSmsEntreprise;
}
