package com.phoenixacces.apps.persistence.entities.module.assurance;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;


@Entity
@Table(name = "t_assurance_produit_assurance", schema = "digitalweb_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "ProduitAssurance", description = "Modele pour le parametrage des produits d'assurances")
public class ProduitAssurance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "type")
    private String type;


    @NonNull
    @Column(name = "produit")
    private String produit;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
