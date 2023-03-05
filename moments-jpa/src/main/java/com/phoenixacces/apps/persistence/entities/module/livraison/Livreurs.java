package com.phoenixacces.apps.persistence.entities.module.livraison;

import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.TypeContrat;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typeengin.TypeEngins;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "t_livraison_livreurs", schema = "digitalweb_gateway_db",
        uniqueConstraints = {
        @UniqueConstraint(
            name = "uk__contact",
            columnNames = {"contact"}
            )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Livreurs implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;

    @NonNull
    @Column(name = "nomPrenoms")
    private String nomPrenoms;

    @NonNull
    @Column(name = "contact", length = 20)
    private String contact;

    @NonNull
    @Column(name = "notifAnniv")
    private boolean notifAnniv;


    @Column(name="connected")
    private boolean connected;


    @Column(name="disponible")
    private boolean disponible;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__livraison_livreurs__entreprise_id"))
    private Entreprises entreprise;

    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__livraison_livreurs__profile_id"))
    private Profile profile;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;

    @Column(name="version", length = 1)
    private int version;

    @NonNull
    @Column(name = "username", length = 30)
    private String username;

    @Transient
    private String email;

    @Transient
    private LocalDate birthdate;


    @ManyToOne
    @JoinColumn(name = "type_contrat_id", foreignKey = @ForeignKey(name = "fk__livraison_livreurs__type_contrat_id"))
    private TypeContrat typeContrat;


    @ManyToOne
    @JoinColumn(name = "type_engin_id", foreignKey = @ForeignKey(name = "fk__livraison_livreurs__type_engin_id"))
    private TypeEngins typeEngin;


    @Column(name = "assureEngin", length = 5)
    private String assureEngin;


    @Column(name = "compagnieAssurance")
    private String compagnieAssurance;


    @Column(name = "dateFinAssurance")
    private LocalDate dateFinAssurance;


    @NonNull
    @Column(name = "tauxComm")
    private double tauxComm;


    @NonNull
    @Column(name = "commission")
    private double commission;
}
