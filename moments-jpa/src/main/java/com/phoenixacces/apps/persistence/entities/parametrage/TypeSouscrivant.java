package com.phoenixacces.apps.persistence.entities.parametrage;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_type_souscrivant", schema = "digitalweb_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk__typesouscrivant", columnNames = {"typesouscrivant"})
})
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "TypeSouscrivant", description = "Modele of TypeSouscrivant")
public class TypeSouscrivant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "typesouscrivant")
    private String typesouscrivant;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
