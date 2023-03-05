package com.phoenixacces.apps.persistence.entities.parametrage;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_duree_contrat", schema = "digitalweb_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk__duree_contrat", columnNames = {"duree_contrat", "duree"})
})
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "DureeContrat", description = "Modele of Durée du contrat")
public class DureeContrat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "duree")
    private int duree;

    @NonNull
    @Column(name = "duree_contrat", length = 20)
    private String dureeContrat;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}