package com.phoenixacces.apps.persistence.entities.module.assurance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "t_assurance_sender_message", schema = "digitalweb_gateway_db")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class SendMessage implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NonNull
    @Column(name = "dateEnvoi")
    private LocalDate dateEnvoi;


    @Column(name = "numeroContact")
    private String numeroContact;


    @Column(name = "message")
    private String message;


    @Column(name = "typeMessage")
    private String typeMessage;


    @Column(name = "nbrePage")
    private double nbrePage;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "entreprise_id", foreignKey = @ForeignKey(name = "fk__assurance_sender_message__entreprise_id"))
    private Entreprises entreprises;


    @Column(name = "statut")
    private EtatLecture statut;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__assurance_sender_message__profile_id"))
    private Profile profile;


    @JsonIgnore
    @Column(name="active")
    private boolean active;


    @JsonIgnore
    @Column(name="version", length = 1)
    private int version;


    @JsonIgnore
    private Instant creation;


    @JsonIgnore
    private Instant lastUpdate;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "porte_feuille_client_id", foreignKey = @ForeignKey(name = "fk__assurance_sender_message__porte_feuille_client_id"))
    private PorteFeuilleClient porteFeuilleClient;
}
