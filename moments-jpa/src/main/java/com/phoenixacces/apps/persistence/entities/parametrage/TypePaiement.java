package com.phoenixacces.apps.persistence.entities.parametrage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_type_paiement", schema = "digitalweb_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk__type_paiement", columnNames = {"typepaiement"})
})
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "TypePaiement", description = "Modele of TypePaiement")
public class TypePaiement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "typepaiement")
    private String typepaiement;

    @JsonIgnore
    @NonNull
    private boolean active;

    @JsonIgnore
    private Instant creation;

    @JsonIgnore
    private Instant lastUpdate;
}
