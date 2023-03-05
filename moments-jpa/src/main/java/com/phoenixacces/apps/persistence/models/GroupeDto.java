package com.phoenixacces.apps.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class GroupeDto implements Serializable {
    private String ordre;
    private String nomGroupe;
    private Long nbreContact;
    private String profileId;
    private Boolean active;
}
