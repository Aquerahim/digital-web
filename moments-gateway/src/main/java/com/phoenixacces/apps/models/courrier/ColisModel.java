package com.phoenixacces.apps.models.courrier;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Setter
@Getter
public class ColisModel implements Serializable {
    private String natureColis;
    private String designationColis;
}
