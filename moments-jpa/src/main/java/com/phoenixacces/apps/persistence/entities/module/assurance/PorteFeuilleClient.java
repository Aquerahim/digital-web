package com.phoenixacces.apps.persistence.entities.module.assurance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Civilite;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "t_assurance_porte_feuille_client", schema = "digitalweb_gateway_db")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class PorteFeuilleClient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "nomCapagne")
    private String nomCapagne;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;


    @ManyToOne
    @JoinColumn(name = "civilite_id", foreignKey = @ForeignKey(name = "fk__assurance_porte_feuille__civilite_id"))
    private Civilite civilite;


    @NonNull
    @Column(name = "nomClient")
    private String nomClient;


    @NonNull
    @Column(name = "contact")
    private String contact;


    @Column(name = "contact2")
    private String contact2;


    @Column(name = "numeroContrat")
    private String numeroContrat;


    @Column(name = "fete")
    private String fete;


    @Column(name = "dateNaissance")
    private LocalDate dateNaissance;


    @ManyToOne
    @JoinColumn(name = "type_client_id", foreignKey = @ForeignKey(name = "fk__assurance_porte_feuille__type_client_id"))
    private TypeClient typeClient;


    @Column(name = "souscritPrdt", length = 5)
    private String souscritPrdt;


    @ManyToOne
    @JoinColumn(name = "produit_id", foreignKey = @ForeignKey(name = "fk__assurance_porte_feuille__produit_id"))
    private ProduitAssurance produit;


    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__assurance_porte_feuille__profile_id"))
    private Profile profile;


    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__assurance_porte_feuille_client__entreprise_id"))
    private Entreprises entreprises;


    @Column(name = "notifMsgBienvenu", length = 5)
    private String notifMsgBienvenu;


    @Column(name = "notifSouscription", length = 5)
    private String notifSouscription;


    @Column(name = "notifAnniv", length = 5)
    private String notifAnniv;


    @Column(name = "notifRappelEcheJ45", length = 5)
    private String notifRappelEcheJ45;


    @Column(name = "notifRappelEcheJ15", length = 5)
    private String notifRappelEcheJ15;


    @Column(name = "notifFeteReligieuse", length = 5)
    private String notifFeteReligieuse;


    @Column(name = "notifFeteNationale", length = 5)
    private String notifFeteNationale;


    @Column(name = "notifRappelVisiteTechnique", length = 5)
    private String notifRappelVisiteTechnique;


    @Column(name = "immatriculation")
    private String immatriculation;


    @Column(name = "dateExpirationVisite")
    private LocalDate dateExpirationVisite;


    @Column(name = "typeVehicule")
    private String typeVehicule;


    @Column(name = "expirationContrat")
    private LocalDate expirationContrat;


    @Column(name = "notifFeteMere", length = 5)
    private String notifFeteMere;


    @Column(name = "notifFetePere", length = 5)
    private String notifFetePere;


    @Column(name="active")
    private boolean active;


    @JsonIgnore
    @Column(name="version", length = 1)
    private int version;


    @JsonIgnore
    private Instant creation;

    @JsonIgnore
    private Instant lastUpdate;


    @JsonIgnore
    @Transient
    private String notifs;
}
