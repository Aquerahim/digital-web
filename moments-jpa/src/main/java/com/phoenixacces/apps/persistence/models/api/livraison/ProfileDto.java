package com.phoenixacces.apps.persistence.models.api.livraison;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "ProfileDto", description = "DTO Profile Get model")
public class ProfileDto implements Serializable {

    @ApiModelProperty(name = "idDigital", position = 1, value = "L'id di-gital web du profile dans le système lors du chargement.")
    private String idDigital;

    @ApiModelProperty(name = "dateNaissance", position = 2, value = "Date de naissance du livreur")
    private String dateNaissance;

    @ApiModelProperty(name = "email", position = 3, value = "email livreur")
    private String email;

    @ApiModelProperty(name = "genre", position = 4, value = "Genre du livreur")
    private String genre;

    @ApiModelProperty(name = "nomPrenoms", position = 5, value = "Nom et prenoms du livreur")
    private String nomPrenoms;

    @ApiModelProperty(name = "contact", position = 6, value = "contact du livreur")
    private String contact;

    @ApiModelProperty(name = "tauxCommission", position = 7, value = "Taux de commision")
    private String tauxCommission;

    @ApiModelProperty(name = "commission", position = 8, value = "Montant de la commision")
    private double commision;

    @ApiModelProperty(name = "commisionGlobal", position = 9, value = "Montant Total possible de la commision")
    private double commisionGlobal;

    @ApiModelProperty(name = "typeProfile", position = 10, value = "Type profile")
    private String typeProfile;

    @ApiModelProperty(name = "enginAssure", position = 11, value = "Champs ramenant si l'engins du livreur est assuré.")
    private String enginAssure;

    @ApiModelProperty(name = "assureur", position = 12, value = "Champs ramenant si l'engins du livreur est assuré.")
    private String assureur;

    @ApiModelProperty(name = "dateFinAssurance", position = 13, value = "Champs ramenant si l'engins du livreur est assuré.")
    private String dateFinAssurance;

    @ApiModelProperty(name = "typeEngin", position = 14, value = "Le type d'engin utilisé par le livreur.")
    private String typeEngin;

    @ApiModelProperty(name = "nbrLivrSucces", position = 15, value = "Nombre de livraison effectué avec succès")
    private int nbrLivrSucces;

    @ApiModelProperty(name = "nbrLivrAnnule", position = 16, value = "Nombre de livraison annulée")
    private int nbrLivrAnnule;

    @ApiModelProperty(name = "nbrLivrAnnule", position = 17, value = "Nombre de livraison annulée")
    private int nbrLivrNoSucces;

    @ApiModelProperty(name = "nbrLivrPending", position = 18, value = "Nombre de livraison assignées")
    private int nbrLivrPending;

    @ApiModelProperty(name = "nbrLivrAssigne", position = 19, value = "Nombre de livraison assignées")
    private int nbrLivrAssigne;

    @ApiModelProperty(name = "notifAnniv", position = 20, value = "Notification de l'annversaire.")
    private String notifAnniv;

    @ApiModelProperty(name = "notifAnniv", position = 21, value = "Notification de l'annversaire.")
    private String appDownload;

    @ApiModelProperty(name = "jwt", position = 23, value = "Notification de l'annversaire.")
    private String jwt;

    @ApiModelProperty(name = "premiereCnx", position = 22, value = "Champs permettant si le mot de passe a été modifier à la prémière connexion. Si Oui le client à déjà fait sa prémière connexion. Sinon modification du mot de passe est obligatoire.")
    private String premiereCnx;
}
