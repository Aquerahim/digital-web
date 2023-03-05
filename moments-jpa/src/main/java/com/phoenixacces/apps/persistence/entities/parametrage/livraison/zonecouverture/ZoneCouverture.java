package com.phoenixacces.apps.persistence.entities.parametrage.livraison.zonecouverture;

import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typezone.TypeZoneCouverture;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_livraison_zone_couverture", schema = "moments_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ZoneCouverture implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "zoneCouverture")
    private String zoneCouverture;

    @ManyToOne
    @JoinColumn(name = "type_zone_couverture_id", foreignKey = @ForeignKey(name = "fk__livraison_zone_couverture__type_zone_couverture_id"))
    private TypeZoneCouverture typeZoneCouverture;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
