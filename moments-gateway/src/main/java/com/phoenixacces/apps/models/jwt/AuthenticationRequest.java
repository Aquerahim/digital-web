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
@ApiModel(value = "AuthenticationRequest", description = "Authentication request model")
public class AuthenticationRequest implements Serializable {
    @ApiModelProperty(name = "username", position = 1, value = "Username.", example = "service")
    private String username;

    @ApiModelProperty(name = "password", position = 2, value = "Password.", example = "password")
    private String password;
}
