package com.phoenixacces.apps.persistence.entities.module.assurance;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_assurance_parametrage_date", schema = "digitalweb_gateway_db")
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "ParametrageDates", description = "Modele pour le parametrage des date de départ")
public class ParametrageDates implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dateDeb", length = 20)
    private String dateDeb;

    @Column(name = "typeDateDeb", length = 70)
    private String typeDateDeb;

    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
