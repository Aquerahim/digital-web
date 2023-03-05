package com.phoenixacces.apps.models.mail;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class EnvoiEmail implements Serializable {
    private String username;
    private String subject;
    private String email;
    private String phone;
    private String demande;
    private String avezVous;
    private String message;
    private String typeDemande;
    private double otp;
}
