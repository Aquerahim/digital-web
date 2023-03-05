package com.phoenixacces.apps.models.jwt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "AuthenticationResponse", description = "Authentication response model")
public class AuthenticationResponse implements Serializable {

    @ApiModelProperty(name = "token", position = 1, value = "Authentication token.", readOnly = true)
    private String token;
}