package com.phoenixacces.apps.persistence.entities.module.livraison;

import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.SuiviDemande;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.zonecouverture.ZoneCouverture;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "t_livraisons_fiche_bon_de_commande", schema = "digitalweb_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk_reference", columnNames = {"reference"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FicheBonDeCommande implements Serializable{

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
    @JoinColumn(name = "partenaire_id", foreignKey = @ForeignKey(name = "fk__livraisons_fiche_bon_de_commande__partenaire_id"))
    private PartenaireAffaire partenaire;


    @ManyToOne
    @JoinColumn(name = "zone_recuperation_id", foreignKey = @ForeignKey(name = "fk__livraisons_fiche_bon_de_commande__zone_recuperation_id"))
    private ZoneCouverture zoneRecuperation;


    @NonNull
    @Column(name = "precisionZoneRecup")
    private String precisionZoneRecup;


    @NonNull
    @Column(name = "datelivraisonSouhaite")
    private LocalDate datelivraisonSouhaite;


    @NonNull
    @Column(name = "typeLivraison")
    private String typeLivraison;


    @ManyToOne
    @JoinColumn(name = "Suivi_demande_id", foreignKey = @ForeignKey(name = "fk__livraisons_fiche_bon_de_commande__Suivi_demande_id"))
    private SuiviDemande suiviDemande;


    @Transient
    @ManyToOne
    @JoinColumn(name = "bon_commande_id", foreignKey = @ForeignKey(name = "fk__livraisons_fiche_bon_de_commande__bn_commande_id"))
    private BonDeCommande bonCommande;



    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__livraisons_fiche_bon_de_commande__profile_id"))
    private Profile profile;


    @NonNull
    @Column(name = "montantGlobal")
    private double montantGlobal;


    @NonNull
    @Column(name = "montantGlobalLivraison")
    private double montantGlobalLivraison;



    @NonNull
    private boolean notification;


    @NonNull
    private boolean active;


    private Instant creation;


    private Instant lastUpdate;


    @Column(name="version", length = 1)
    private int version;


    @Column(name="nbreArticle", length = 1)
    private int nbreArticle;


    @Transient
    private List<BonDeCommande> bonDeCommandeList;

    @Transient
    private Long ownerNumber;


    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__livraisons_fiche_bon_de_commande__entreprise_id"))
    private Entreprises entreprise;


    @Transient
    private SuiviDemande statuTraitement;
}
