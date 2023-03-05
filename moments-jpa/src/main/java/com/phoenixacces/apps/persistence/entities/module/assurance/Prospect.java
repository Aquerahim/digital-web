package com.phoenixacces.apps.persistence.entities.module.assurance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "t_assurance_prospect", schema = "digitalweb_gateway_db")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Prospect implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;

    @ManyToOne
    @JoinColumn(name = "type_prospect_id", foreignKey = @ForeignKey(name = "fk__t_assurance_prospect__type_prospect_id"))
    private TypeClient typeProspect;

    @NonNull
    @Column(name = "nomPrenoms")
    private String nomPrenoms;


    @Column(name = "adresse")
    private String adresse;


    @Column(name = "contact")
    private String contact;


    @Column(name = "dateNaissance")
    private LocalDate dateNaissance;

    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__assurance_prospect__profile_id"))
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__assurance_prospect__entreprise_id"))
    private Entreprises entreprises;

    @ManyToOne
    @JoinColumn(name = "groupe_id", foreignKey = @ForeignKey(name = "fk__assurance_prospect__groupe_id"))
    private Groupe groupe;

    @ManyToOne
    @JoinColumn(name = "produit_id", foreignKey = @ForeignKey(name = "fk__assurance_prospect__produit_id"))
    private ProduitAssurance produit;

    @Column(name="active")
    private boolean active;


    @JsonIgnore
    @Column(name="version", length = 1)
    private int version;


    @JsonIgnore
    private Instant creation;

    @JsonIgnore
    private Instant lastUpdate;
}
