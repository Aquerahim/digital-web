package com.phoenixacces.apps.persistence.entities.service;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_type_message", schema = "moments_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk__typemessgae", columnNames = {"typemessgae"})
})
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "TypeMessage", description = "Modele of TypeMessage")
public class TypeMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "typemessgae")
    private String typemessgae;

    @Column(name = "module")
    private String module;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
