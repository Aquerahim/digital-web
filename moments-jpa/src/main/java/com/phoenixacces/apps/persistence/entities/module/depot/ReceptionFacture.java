package com.phoenixacces.apps.persistence.entities.module.depot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "t_dpbxon_reception_facture", schema = "digitalweb_gateway_db")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class ReceptionFacture implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;


    @NonNull
    @Column(name = "numeroFacture")
    private String numeroFacture;



    @Column(name = "notifFournisseur")
    private String notifFournisseur;



    @Column(name = "typeNotif")
    private String typeNotif;



    @NonNull
    @Column(name = "dateReception")
    private LocalDate dateReception;


    @ManyToOne
    @JoinColumn(name = "fournisseur_id", foreignKey = @ForeignKey(name = "fk__dpbxon_reception_facture__fournisseur_id"))
    private Fournisseur fournisseur;



    @NonNull
    @Column(name = "statutFacture")
    private String statutFacture;



    @NonNull
    @Column(name = "accompteFacture")
    private double accompteFacture;



    @NonNull
    @Column(name = "montantRestant")
    private double montantRestant;



    @NonNull
    @Column(name = "montantTotalFacture")
    private double montantTotalFacture;



    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__dpbxon_reception_facture__profile_id"))
    private Profile profile;



    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__dpbxon_reception_facture__entreprise_id"))
    private Entreprises entreprises;



    @Column(name="active")
    private Boolean active;



    @JsonIgnore
    @Column(name="version", length = 1)
    private int version;



    @JsonIgnore
    private Instant creation;


    @JsonIgnore
    private Instant lastUpdate;


    @Transient
    private List<Article> articleList;
}
