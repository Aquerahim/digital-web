package com.phoenixacces.apps.persistence.entities.module.assurance;

import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.service.TypeMessage;
import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;


@Entity
@Table(name = "t_assurance_modele_message", schema = "digitalweb_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "ModelMessage", description = "Modele pour le parametrage des modèle de message d'assurances")
public class ModelMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "libelle")
    private String libelle;

    @NonNull
    @Column(name = "message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "type_message_id", foreignKey = @ForeignKey(name = "fk__assurance_modele_message__type_message_id"))
    private TypeMessage typeMessage;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__assurance_modele_message__entreprise_id"))
    private Entreprises entreprises;


    @Column(name = "nbrePage")
    private int nbrePage;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;

}
