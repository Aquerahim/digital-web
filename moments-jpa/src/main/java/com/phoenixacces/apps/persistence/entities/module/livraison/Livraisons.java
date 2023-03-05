package com.phoenixacces.apps.persistence.entities.module.livraison;

import com.phoenixacces.apps.enumerations.StatutLivraison;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.colis.NatureColis;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.zonecouverture.ZoneCouverture;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "t_livraisons_mdle", schema = "digitalweb_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk_reference", columnNames = {"reference"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Livraisons implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;


    @NonNull
    @Column(name = "reference", length = 20)
    private String reference;


    @ManyToOne
    @JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "fk__livraisons__client_id"))
    private PartenaireAffaire client;


    @ManyToOne
    @JoinColumn(name = "zoneLivraison_id", foreignKey = @ForeignKey(name = "fk__livraisons__zoneLivraison_id"))
    private ZoneCouverture zoneLivraison;


    @NonNull
    @Column(name = "precisonLieuLivraison")
    private String precisonLieuLivraison;


    @ManyToOne
    @JoinColumn(name = "zoneRecuperation_id", foreignKey = @ForeignKey(name = "fk__livraisons__zoneRecuperation_id"))
    private ZoneCouverture zoneRecuperation;


    @NonNull
    @Column(name = "precisionZoneRecup")
    private String precisionZoneRecup;


    @ManyToOne
    @JoinColumn(name = "natureColis_id", foreignKey = @ForeignKey(name = "fk__livraisons__natureColis_id"))
    private NatureColis natureColis;


    @NonNull
    @Column(name = "descriptionColis")
    private String descriptionColis;


    @NonNull
    @Column(name = "qte")
    private int qte;


    @NonNull
    @Column(name = "prixLivraison")
    private double prixLivraison;


    @NonNull
    @Column(name = "montantColis")
    private double montantColis;


    @NonNull
    @Column(name = "commissionLivreur")
    private double commissionLivreur;

    @NonNull
    @Column(name = "nomPrenomsDestinataire")
    private String nomPrenomsDestinataire;

    @NonNull
    @Column(name = "contactDestinataire")
    private String contactDestinataire;


    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__livraisons__entreprise_id"))
    private Entreprises entreprise;


    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__livraisons__profile_id"))
    private Profile profile;


    @ManyToOne
    @JoinColumn(name = "livreur_id", foreignKey = @ForeignKey(name = "fk__livraisons__livreur_id"))
    private Livreurs livreur;


    @Column(name = "statutLivraison")
    private StatutLivraison statutLivraison;


    @Column(name = "motifNonLivraison")
    @Lob
    private String motifNonLivraison;


    @Column(name = "infoComplementaire")
    private String infoComplementaire;


    @Column(name = "notifClient")
    private String notifClient;


    @NonNull
    private boolean active;


    private Instant creation;


    private Instant lastUpdate;


    @Column(name="version", length = 1)
    private int version;


    @Column(name="etatNotifArchemi", length = 30)
    private String etatNotifArchemi;


    @Column(name="notifs", length = 30)
    private String notifs;


    @NonNull
    @Column(name = "datelivraison")
    private LocalDate datelivraison;


   /* @Transient
    private Long clientId;*/


    @Transient
    private String action;


    @Column(name="messageRemerciement")
    private int messageRemerciement;


    @Transient
    private StatutLivraison statutRefus;
}
