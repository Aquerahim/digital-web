package com.phoenixacces.apps.persistence.entities.parametrage;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_type_contrat", schema = "digitalweb_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "SmsCredential", description = "Modele pour le parametrage des types de contrat")
public class TypeContrat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "typecontrat")
    private String typecontrat;


    @Column(name = "module", length = 20)
    private String module;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
