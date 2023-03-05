package com.phoenixacces.apps.persistence.entities.parametrage;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_civilite", schema = "digitalweb_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "Civilite", description = "Modele pour le parametrage des Civilite")
public class Civilite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "civilite")
    private String civilite;


    @Column(name = "codeCivilite", length = 10)
    private String codeCivilite;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
