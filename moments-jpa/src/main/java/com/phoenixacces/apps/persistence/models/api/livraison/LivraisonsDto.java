package com.phoenixacces.apps.persistence.models.api.livraison;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "LivraisonsDto", description = "DTO Livraisons Get model")
public class LivraisonsDto implements Serializable {

    @ApiModelProperty(name = "id", position = 1, value = "L'id de la livraison dans le système lors du chargement.")
    private String id;

    @ApiModelProperty(name = "statut", position = 2, value = "Le statut de la livraison du client dans le système lors du chargement.")
    private String statut;

    @ApiModelProperty(name = "lieu", position = 3, value = "Le lieu de la livraison du colis du client dans le système lors du chargement.")
    private String lieu;

    @ApiModelProperty(name = "dateAttribution", position = 4, value = "La date attribution de la livraison du colis du client dans le système lors du chargement.")
    private String date_attribution;

    @ApiModelProperty(name = "date_livraison", position = 5, value = "La date de livraison de la livraison du colis du client dans le système lors du chargement.")
    private String date_livraison;

    //@JsonIgnore
    @ApiModelProperty(name = "dateLivraison", position = 6, value = "La date de livraison de la livraison du colis du client dans le système lors du chargement.")
    private String dateLivraison;

    @ApiModelProperty(name = "client", position = 7, value = "Les informations du clients lors de la livraison de son colis dans le système lors du chargement.")
    private Client client;

    @ApiModelProperty(name = "dateLivraison", position = 8, value = "La liste des produit du clients lors de la livraison de son colis dans le système lors du chargement.")
    private List<Produits> produits;

    @ApiModelProperty(name = "commission", position = 9, value = "La commission du livreur sur la livraison dans le système lors du chargement.")
    private double commission;
}
