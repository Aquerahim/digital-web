package com.phoenixacces.apps.persistence.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Getter
@Setter
public class SendMessageDto implements Serializable {
    private String code;
    private String nomPrenoms;
    private String contact;
    private String statut;
    private String entreprise;
    private String auteur;
    private String userName;
    private LocalDate dateEnvoi;
    private String typeMessage;
    private String message;
    private String page;
}
