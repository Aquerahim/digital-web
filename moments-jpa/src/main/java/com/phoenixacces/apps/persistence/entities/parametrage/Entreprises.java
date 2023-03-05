package com.phoenixacces.apps.persistence.entities.parametrage;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "t_entreprise", schema = "digitalweb_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk_libelle", columnNames = {"compagnie", "rccm", "abbrev"})
})
@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Entreprises implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="compagnie", nullable = false)
    private String compagnie;


    @Column(name="abbrev", nullable = false)
    private String abbrev;


    @Column(name="rccm",length = 30)
    private String rccm;


    @Column(name="contact", nullable = false, length = 30)
    private String contact;


    @Column(name="adresse")
    private String adresse;


    @Column(name="bp")
    private String bp;


    @Column(name="email")
    private String email;


    @Column(name="web")
    private String web;


    @NonNull
    @Column(name="active", nullable = false)
    private boolean active;

    @NonNull
    @Column(name="version", length = 1, nullable = false)
    private int version;


    @Column(name="logo")
    private String logo;


    @Column(name="responsable")
    private String responsable;


    @Column(name="contact_responsable")
    private String contactResponsable;


    @ManyToOne
    @JoinColumn(name = "type_contrat_id", foreignKey = @ForeignKey(name = "fk_compagnie_routiere__type_contrat_id"))
    private TypeContrat typeContrat;


    @NonNull
    @Column(name = "dateeffetcontrat")
    private LocalDate dateEffetContrat;

    @ManyToOne
    @JoinColumn(name = "motif_suspension_collaboration_id", foreignKey = @ForeignKey(name = "fk_compagnie_routiere__motif_suspension_collaboration_id"))
    private MotifSuspensionCollaboration motifSuspensionCollaboration;


    @ManyToOne
    @JoinColumn(name = "sms_credential_id", foreignKey = @ForeignKey(name = "fk_compagnie_routiere__sms_credential_id"))
    private SmsCredential smsCredential;


    @ManyToOne
    @JoinColumn(name = "pays_autorise_id", foreignKey = @ForeignKey(name = "fk_compagnie_routiere__pays_autorise_id"))
    private PaysAutorise paysAutorise;


    @ManyToOne
    @JoinColumn(name = "type_souscrivant_id", foreignKey = @ForeignKey(name = "fk_compagnie_routiere__type_souscrivant_id"))
    private TypeSouscrivant typeSouscrivant;


    private Instant creation;


    private Instant lastUpdate;


    @Column(name="slogan")
    private String slogan;


    @ManyToOne
    @JoinColumn(name = "duree_contrat_id", foreignKey = @ForeignKey(name = "fk_compagnie_routiere__duree_contrat_id"))
    private DureeContrat dureeContrat;

    @NonNull
    @Column(name="taciteReconduit", nullable = false)
    private boolean taciteReconduit;

    @Column(name="tauxCommLivreur")
    private double tauxCommLivreur;


    @Column(name = "datefincontrat")
    private Date dateFinContrat;


    @Column(name = "dateRappelFinContrat")
    private LocalDate dateRappelFinContrat;

    @Column(name = "notificateur")
    private Long notificateur;
}
