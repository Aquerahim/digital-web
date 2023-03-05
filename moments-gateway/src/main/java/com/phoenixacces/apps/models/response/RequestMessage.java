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
@ApiModel(value = "RequestMessage", description = "Request model")
public class RequestMessage implements Serializable {

    @ApiModelProperty(name = "code", position = 1, value = "Return code of the transaction.", readOnly = true)
    private String code;

    @ApiModelProperty(name = "contractNumber", position = 2, value = "Describes the return code of the transaction.", readOnly = true)
    private String description;
}
