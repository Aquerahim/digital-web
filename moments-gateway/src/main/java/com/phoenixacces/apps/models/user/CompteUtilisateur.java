package com.phoenixacces.apps.models.user;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class CompteUtilisateur implements Serializable {
    private String nomPrenoms;
    private String phone;
    private String idDigital;
    /*private String facebook;
    private String twitter;
    private String instagram;
    private String skype;
    private String email;*/
}
