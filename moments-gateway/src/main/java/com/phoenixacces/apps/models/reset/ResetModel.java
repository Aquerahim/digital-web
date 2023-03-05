package com.phoenixacces.apps.models.reset;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@ToString
@ApiModel(value = "ResetModel", description = "Reset request model")
public class ResetModel implements Serializable {
    private String email;
    private String codeOTP;
    private String telephone;
}
