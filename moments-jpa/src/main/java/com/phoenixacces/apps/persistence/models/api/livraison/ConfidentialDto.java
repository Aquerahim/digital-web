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
@ApiModel(value = "ConfidentialDto", description = "DTO Confidential Get model")
public class ConfidentialDto implements Serializable {

    @ApiModelProperty(name = "pc", position = 1, value = "Politique de confidentialité et de protection des données.")
    private String pc;

    @ApiModelProperty(name = "cgu", position = 2, value = "Condition générale d'utilisation")
    private String cgu;
}
