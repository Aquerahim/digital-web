package com.phoenixacces.apps.persistence.entities.parametrage;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;


@Entity
@Table(name = "t_motif_suspension_collaboration", schema = "digitalweb_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "MotifSuspensionCollaboration", description = "Modele pour le parametrage des types de motif de suspension de collaboration")
public class MotifSuspensionCollaboration implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "motif")
    private String motif;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}