package com.phoenixacces.apps.persistence.entities.module.depot;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;


@Entity
@Table(name = "t_dpbxon_produit", schema = "digitalweb_gateway_db")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Produit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;


    @NonNull
    @Column(name = "nomBoisson")
    private String nomBoisson;


    @NonNull
    @Column(name = "peremption")
    private LocalDate peremption;


    @NonNull
    @Column(name = "prixAchat")
    private double prixAchat;


    @NonNull
    @Column(name = "prixVente")
    private double prixVente;


    @Column(name = "prixVteDemiGros")
    private double prixVteDemiGros;


    @Column(name = "prixVteEnGros")
    private double prixVteEnGros;


    @Column(name = "montantVendu")
    private double montantVendu;


    @Column(name = "montantVenduEspere")
    private double montantVenduEspere;


    @Column(name = "qteEnStock")
    private int qteEnStock;


    @Column(name = "seuilAlerte")
    private int seuilAlerte;


    @ManyToOne
    @JoinColumn(name = "categorie_id", foreignKey = @ForeignKey(name = "fk__dpbxon_produit__categorie_id"))
    private CategorieBoisson categorie;


    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__dpbxon_produit__profile_id"))
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__dpbxon_produit__entreprise_id"))
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

    @Column(name = "msgAlerte")
    private String msgAlerte;
}
