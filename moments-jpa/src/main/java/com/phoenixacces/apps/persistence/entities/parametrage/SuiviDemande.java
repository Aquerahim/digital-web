package com.phoenixacces.apps.persistence.entities.parametrage;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_suivi_demande", schema = "digitalweb_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "SuiviDemande", description = "Modele pour le parametrage des Suivis de demande")
public class SuiviDemande implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "suivi")
    private String suivi;


    @Column(name = "mdle")
    private String mdle;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
