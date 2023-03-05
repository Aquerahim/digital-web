package com.phoenixacces.apps.models.response;

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
@ApiModel(value = "RequestResponse", description = "RequestResponse model")
public class RequestResponse implements Serializable {

    @ApiModelProperty(name = "information", position = 1, value = "informations requête", readOnly = true)
    private RequestInformation information;

    @ApiModelProperty(name = "message", position = 2, value = "Statut de l'API de la transaction ", readOnly = true)
    private RequestMessage message;
}

