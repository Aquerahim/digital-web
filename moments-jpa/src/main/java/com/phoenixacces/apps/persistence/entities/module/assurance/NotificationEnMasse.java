package com.phoenixacces.apps.persistence.entities.module.assurance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "t_assurance_notification_message_en_masse", schema = "digitalweb_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "NotificationEnMasse", description = "NotificationEnMasse  message d'assurances")
public class NotificationEnMasse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "ordre")
    private Long ordre;

    @NonNull
    @Column(name = "nomCampagne")
    private String nomCampagne;


    @NonNull
    @Column(name = "sourceData")
    private String sourceData;



    @Column(name = "typeMessage")
    private String typeMessage;


    @Column(name = "inspirationModele")
    private String inspirationModele;


    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__assurance_notification_message_en_masse__profile_id"))
    private Profile profile;


    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__assurance_notification_message_en_masse__entreprise_id"))
    private Entreprises entreprises;


    @Column(name = "nomGroup")
    private String nomGroup;


    @NonNull
    @Column(name = "dateEnvoi")
    private LocalDate dateEnvoi;

    @Column(name = "message")
    @Lob
    private String message;


    @NonNull
    private Boolean active;


    private Instant creation;


    private Instant lastUpdate;


    @Transient
    private Groupe groupeProspect;
}
