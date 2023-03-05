package com.phoenixacces.apps.persistence.entities.module.depot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_dpbxon_compte_banqcaire", schema = "digitalweb_gateway_db")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class CompteBancaire implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;


    @ManyToOne
    @JoinColumn(name = "banque_id", foreignKey = @ForeignKey(name = "fk__dpbxon_compte_banqcaire__banque_id"))
    private Banque banque;


    @NonNull
    @Column(name = "numeroDeCompte")
    private String numeroDeCompte;


    @NonNull
    @Column(name = "nomGestionnaire")
    private String nomGestionnaire;


    @NonNull
    @Column(name = "contactGestionnaire")
    private String contactGestionnaire;


    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__dpbxon_compte_banqcaire__profile_id"))
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__dpbxon_compte_banqcaire__entreprise_id"))
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
}
