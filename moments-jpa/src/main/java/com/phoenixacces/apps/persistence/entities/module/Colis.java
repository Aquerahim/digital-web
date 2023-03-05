package com.phoenixacces.apps.persistence.entities.module;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_colis_envoye", schema = "digitalweb_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Colis implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "colisNumber")
    private String colisNumber;


    @Column(name = "natureColis")
    private String natureColis;


    @Column(name = "designationColis")
    private String designationColis;


    @Column(name="active")
    private boolean active;


    @Column(name="version", length = 1)
    private int version;


    private Instant creation;


    private Instant lastUpdate;
}
