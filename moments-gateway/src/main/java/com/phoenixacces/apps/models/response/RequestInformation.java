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
@ApiModel(value = "RequestInformation", description = "Request information model")
public class RequestInformation implements Serializable {
    @ApiModelProperty(name = "requestCode", position = 1, value = "Request Code.", readOnly = true)
    private long requestCode;

    @ApiModelProperty(name = "information", position = 2, value = "information.", readOnly = true)
    private String message;
}
