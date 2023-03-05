package com.phoenixacces.apps.persistence.entities.module.depot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_dpbxon_article", schema = "digitalweb_gateway_db")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Article implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;


    @NonNull
    @Column(name = "article")
    private String article;


    @Column(name = "quantite")
    private int quantite;

    @NonNull
    @Column(name = "montant")
    private double montant;

    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__dpbxon_reception_facture__profile_id"))
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__dpbxon_reception_facture__entreprise_id"))
    private Entreprises entreprises;

    @JsonIgnore
    @Column(name="active")
    private Boolean active;


    @JsonIgnore
    @Column(name="version", length = 1)
    private int version;


    @JsonIgnore
    private Instant creation;

    @JsonIgnore
    private Instant lastUpdate;
}
