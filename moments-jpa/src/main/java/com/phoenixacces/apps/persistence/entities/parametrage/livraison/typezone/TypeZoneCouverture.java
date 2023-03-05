package com.phoenixacces.apps.persistence.entities.parametrage.livraison.typezone;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_livraison_type_zone_couverture", schema = "moments_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TypeZoneCouverture implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "typezone")
    private String typezone;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
