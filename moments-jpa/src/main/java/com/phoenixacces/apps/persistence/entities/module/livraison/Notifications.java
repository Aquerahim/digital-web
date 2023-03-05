package com.phoenixacces.apps.persistence.entities.module.livraison;

import com.phoenixacces.apps.enumerations.StatutLivraison;
import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "t_notification", schema = "digitalweb_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Notifications implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ref")
    private String ref;

    @NonNull
    @Column(name = "nomDestinataire")
    private String nomDestinataire;

    @NonNull
    @Column(name = "contact")
    private String contact;

    @NonNull
    @Column(name = "nomLivreur")
    private String nomLivreur;


    @Column(name = "contactLivreur")
    private String contactLivreur;


    @Column(name = "typeNotification")
    private TypeNotification typeNotification;


    @Column(name = "statutEnvoi")
    private StatutLivraison statutEnvoi;


    @Column(name = "dateEnvoi")
    private Date dateEnvoi;

    @NonNull
    private boolean envoi;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;

    @NonNull
    @Column(name = "module")
    private String module;


    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__livraisons__profile_id"))
    private Profile profile;
}
