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
@ApiModel(value = "CommissionDto", description = "DTO Commission Get model")
public class CommissionDto implements Serializable {

    @ApiModelProperty(name = "commision", position = 1, value = "Commission du livreur à date.")
    private double commision;

    @ApiModelProperty(name = "dateDemande", position = 2, value = "Date de la mise à jour de la commission du livreur")
    private String dateDemande;

    @ApiModelProperty(name = "succesLivraison", position = 3, value = "Nombre de livraison effectuée")
    private int succesLivraison;
}
