package com.phoenixacces.apps.persistence.entities.parametrage;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_sms_credential_entreprise", schema = "digitalweb_gateway_db")
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "SmsCredential", description = "Modele pour le parametrage des sms à envoyé")
public class SmsCredential implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "username")
    private String username;

    @NonNull
    @Column(name = "password")
    private String password;

    @NonNull
    @Column(name = "senderId")
    private String senderId;

    @NonNull
    @Column(name = "senderLeTexto")
    private String senderLeTexto;

    @NonNull
    @Column(name = "ref")
    private String ref;

    @Column(name = "nombreSms")
    private Long nombreSms;


    @Column(name = "nombreSmsLeTexto")
    private Long nombreSmsLeTexto;


    @Column(name = "token")
    private String token;

    @NonNull
    @Column(name = "affected")
    private boolean affected;


    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
