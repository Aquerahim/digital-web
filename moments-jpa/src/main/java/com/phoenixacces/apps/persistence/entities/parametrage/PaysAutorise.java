package com.phoenixacces.apps.persistence.entities.parametrage;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_pays_autorise", schema = "digitalweb_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "SmsCredential", description = "Modele pour le parametrage des pays autorisé")
public class PaysAutorise implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "indicatif", length = 10)
    private String indicatif;

    @NonNull
    @Column(name = "paysautorise")
    private String paysautorise;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
