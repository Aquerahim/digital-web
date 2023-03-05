package com.phoenixacces.apps.persistence.entities.module.livraison;

import com.phoenixacces.apps.enumerations.ProfileType;
import com.phoenixacces.apps.enumerations.TypeClientLivraison;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typezone.TypeZoneCouverture;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.zonecouverture.ZoneCouverture;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "t_livraison_partenaire_d_affaire", schema = "digitalweb_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PartenaireAffaire implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;


    @NonNull
    @Column(name = "nomComplet")
    private String nomComplet;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @NonNull
    @Column(name = "typeClient")
    private TypeClientLivraison typeClient;

    @NonNull
    @Column(name = "activite")
    private String activite;

    @NonNull
    @Column(name = "contact", length = 30)
    private String contact;

    @NonNull
    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "zone_couverture_id", foreignKey = @ForeignKey(name = "fk__livraison_mes_client__zone_couverture_id"))
    private ZoneCouverture zoneCouverture; //INTERIEUR DU PAYS / Grand Abidjan

    @NonNull
    @Column(name = "situationGeo")
    private String situationGeo;


    @Column(name = "precisonZone")
    private String precisonZone;

    @NonNull
    @Column(name = "nomResponsable")
    private String nomResponsable;

    @NonNull
    @Column(name = "contactResponsable")
    private String contactResponsable;

    @NonNull
    @Column(name = "notifAnniv")
    private boolean notifAnniv;


    @Column(name="active")
    private boolean active;


    @Column(name="connected")
    private boolean connected;


    @Column(name="version", length = 1)
    private int version;


    private Instant creation;


    private Instant lastUpdate;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__livraison_mes_client__entreprise_id"))
    private Entreprises entreprise;

    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__livraison_mes_client__profile_id"))
    private Profile profile;

    @Transient
    private TypeZoneCouverture typeZoneCouverture;

    @Transient
    private ProfileType profileType;

    @Transient
    private String username;

    @NonNull
    @Column(name = "liaison")
    private String liaison;
}
