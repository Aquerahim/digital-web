package com.phoenixacces.apps.persistence.entities.module.assurance;

import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_assurance_slogan_entreprise", schema = "digitalweb_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "SloganEntreprise", description = "Modele pour le parametrage des slogan pour l'entreprise souscriptrice d'assurances")
public class SloganEntreprise implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "slogan")
    private String slogan;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__assurance_modele_message__entreprise_id"))
    private Entreprises entreprises;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
