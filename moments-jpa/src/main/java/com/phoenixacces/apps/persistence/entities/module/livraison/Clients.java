package com.phoenixacces.apps.persistence.entities.module.livraison;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_livraison_clients", schema = "digitalweb_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Clients implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;

    @NonNull
    @Column(name = "ordreClient", length = 10)
    private Long ordreClient;

    @NonNull
    @Column(name = "destinataire")
    private String destinataire;


    @NonNull
    @Column(name = "contactDestinataire")
    private String contactDestinataire;

    @NonNull
    @Column(name = "nomProduit")
    private String nomProduit;


    @NonNull
    @Column(name = "montantColis")
    private double montantColis;


    @NonNull
    private boolean active;


    private Instant creation;


    private Instant lastUpdate;


    @Column(name="version", length = 1)
    private int version;


    @ManyToOne
    @JoinColumn(name = "partenaire_id", foreignKey = @ForeignKey(name = "fk__livraisons_fiche_bon_de_commande__partenaire_id"))
    private PartenaireAffaire partenaire;
}
