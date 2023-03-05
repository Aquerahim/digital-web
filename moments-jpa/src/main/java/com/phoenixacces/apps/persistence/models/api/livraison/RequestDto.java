package com.phoenixacces.apps.persistence.models.api.livraison;

import com.phoenixacces.apps.enumerations.StatutLivraison;
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
@ApiModel(value = "RequestDto", description = "DTO Request Get model")
public class RequestDto implements Serializable {

    @ApiModelProperty(name = "ref", position = 1, value = "Référence de la livraison à traiter", required = true)
    private String ref;

    @ApiModelProperty(name = "statut", position = 2, value = "Le statut de la livraison qui doit être traiter. Valeur possible sont : <br/>- LIVRE<br/>- ANNULER<br/>- NON_LIVRE", example = "LIVRE", allowableValues = ("LIVRE, ANNULER, NON_LIVRE"), required = true)
    private StatutLivraison statut;
}
