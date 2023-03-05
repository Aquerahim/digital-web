package com.phoenixacces.apps.models.courrier;

import lombok.*;

import java.io.Serializable;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RechercheCourrierModel implements Serializable {
    private Long ordre;
    private String reference;
    private String nomDest;
    private String telDest;
    private Long tarckingSuivant;
}
