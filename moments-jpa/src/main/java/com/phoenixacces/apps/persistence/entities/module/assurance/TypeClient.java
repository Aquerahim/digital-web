package com.phoenixacces.apps.persistence.entities.module.assurance;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;


@Entity
@Table(name = "t_assurance_type_client", schema = "digitalweb_gateway_db")
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "TypeClient", description = "Modele pour le parametrage des type de client d'assurances")
public class TypeClient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "type")
    private String type;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
