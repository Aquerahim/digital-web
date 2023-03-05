package com.phoenixacces.apps.persistence.entities.module.livraison;

import com.phoenixacces.apps.persistence.entities.parametrage.livraison.colis.NatureColis;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typezone.TypeZoneCouverture;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.zonecouverture.ZoneCouverture;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_livraisons_bon_de_commande", schema = "digitalweb_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BonDeCommande implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;


    @ManyToOne
    @JoinColumn(name = "nature_colis_id", foreignKey = @ForeignKey(name = "fk__livraisons_fiche_bon_de_commande__partenaire_id"))
    private NatureColis natureColis;


    @NonNull
    @Column(name = "nomProduit")
    private String nomProduit;


    @ManyToOne
    @JoinColumn(name = "zone_livraison_id", foreignKey = @ForeignKey(name = "fk__livraisons_bon_de_commande__zone_livraison_id"))
    private ZoneCouverture zoneLivraison;


    //@NonNull
    @Column(name = "lieuDeLivraison")
    private String lieuDeLivraison;


    @NonNull
    @Column(name = "elementCassable", length = 4)
    private String elementCassable;


    @NonNull
    @Column(name = "montantColis")
    private double montantColis;


    @NonNull
    @Column(name = "prixLivraison")
    private double prixLivraison;


    @NonNull
    @Column(name = "destinataire")
    private String destinataire;


    @NonNull
    @Column(name = "contactDestinataire")
    private String contactDestinataire;


    @Column(name="active")
    private boolean active;


    @Column(name="version", length = 1)
    private int version;


    private Instant creation;


    private Instant lastUpdate;

    @Transient
    private TypeZoneCouverture typeZoneLiv;
}
