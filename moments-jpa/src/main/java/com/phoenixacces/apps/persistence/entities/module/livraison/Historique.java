package com.phoenixacces.apps.persistence.entities.module.livraison;

import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.persistence.entities.parametrage.TypeContrat;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_livraison_historique", schema = "digitalweb_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Historique implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;


    @NonNull
    @Lob
    @Column(name = "libelle")
    private String libelle;


    @ManyToOne
    @JoinColumn(name = "livraison_id", foreignKey = @ForeignKey(name = "fk__livraison_historique__livraison_id"))
    private Livraisons livraisons;


    @Column(name = "typeNotification")
    private TypeNotification typeNotification;


    @NonNull
    private boolean active;


    private Instant creation;


    private Instant lastUpdate;


    @Column(name="version", length = 1)
    private int version;


    @ManyToOne
    @JoinColumn(name = "fiche_bon_commande_id", foreignKey = @ForeignKey(name = "fk__livraison_historique__fiche_bon_commande_id"))
    private FicheBonDeCommande ficheBonDeCommande;
}
