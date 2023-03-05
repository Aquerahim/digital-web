package com.phoenixacces.apps.persistence.entities.module.depot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_dpbxon_motif_avance", schema = "digitalweb_gateway_db")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class MotifAvance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre", length = 10)
    private Long ordre;


    @NonNull
    @Column(name = "motif")
    private String motif;


    @Column(name="active")
    private Boolean active;


    @JsonIgnore
    @Column(name="version", length = 1)
    private int version;


    @JsonIgnore
    private Instant creation;

    @JsonIgnore
    private Instant lastUpdate;
}
