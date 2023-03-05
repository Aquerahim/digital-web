package com.phoenixacces.apps.persistence.entities.parametrage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_offre_sms_entreprise", schema = "digitalweb_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "OffreSmsEntreprise", description = "Modele of Offre entreprise Sms")
public class OffreSmsEntreprise implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordre")
    private String ordre;


    @Column(name = "nbre_sms")
    private int nbreSms;

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


    @NonNull
    private Boolean active;

    @JsonIgnore
    private Instant creation;

    @JsonIgnore
    private Instant lastUpdate;
}
