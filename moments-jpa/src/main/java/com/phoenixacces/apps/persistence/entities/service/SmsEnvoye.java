package com.phoenixacces.apps.persistence.entities.service;

import com.phoenixacces.apps.persistence.entities.module.ServiceCourrier;
import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_sms_envoye", schema = "moments_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "SmsEnvoye", description = "Modele of SmsEnvoye")
public class SmsEnvoye implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "expediteur")
    private String expediteur;

    @ManyToOne
    @JoinColumn(name = "type_message_id", foreignKey = @ForeignKey(name = "fk_sms_envoye__type_message"))
    private TypeMessage typeMessage;

    @NonNull
    @Column(name = "destinataire")
    private String destinataire;

    @NonNull
    @Column(name = "numero_destinataire")
    private String numeroDestinataire;

    @NonNull
    @Lob
    @Column(name = "corps_message", length = 512, columnDefinition = "text")
    private String corpsMessage;

    @NonNull
    @Column(name = "response_code")
    private int responseCode;

    @NonNull
    @Column(name = "response_successful")
    private boolean responseSuccessful;

    @NonNull
    private boolean active;

    @Column(name = "date_envoie")
    private Instant creation;

    private Instant lastUpdate;

    @ManyToOne
    @JoinColumn(name = "sms_credential_id", foreignKey = @ForeignKey(name = "fk_sms_envoye__sms_credential"))
    private SmsCredential smsCredential;

    @ManyToOne
    @JoinColumn(name = "service_courier_id", foreignKey = @ForeignKey(name = "fk_sms_envoye__service_courier_id"))
    private ServiceCourrier serviceCourrier;

    @NonNull
    @Column(name = "delivery")
    private String delivery;
}
