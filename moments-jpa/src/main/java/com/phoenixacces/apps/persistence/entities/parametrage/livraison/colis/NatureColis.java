package com.phoenixacces.apps.persistence.entities.parametrage.livraison.colis;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_livraison_nature_colis", schema = "moments_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NatureColis implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "typecolis")
    private String typecolis;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}