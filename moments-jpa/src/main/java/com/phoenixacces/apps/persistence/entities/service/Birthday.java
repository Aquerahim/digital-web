package com.phoenixacces.apps.persistence.entities.service;

import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.sun.istack.Nullable;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_birthday", schema = "moments_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Birthday implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Nullable
    @Column(name="birthday", length = 5)
    private String birthday;


    @Column(name="anniversaireux")
    private String anniversaireux;


    @Column(name="annee")
    private String annee;



    @Column(name="envoi")
    private boolean envoi;


    @Column(name="active")
    private boolean active;


    @Column(name="version", length = 1)
    private int version;


    private Instant creation;


    private Instant lastUpdate;
}
