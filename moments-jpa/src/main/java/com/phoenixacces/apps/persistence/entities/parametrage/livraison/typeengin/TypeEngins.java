package com.phoenixacces.apps.persistence.entities.parametrage.livraison.typeengin;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_livraison_type_engins", schema = "moments_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk_type_engins", columnNames = {"typeengins"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TypeEngins implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "typeengins")
    private String typeengins;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
