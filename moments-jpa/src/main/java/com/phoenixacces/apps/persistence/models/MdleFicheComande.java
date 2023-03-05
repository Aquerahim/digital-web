package com.phoenixacces.apps.persistence.models;

import com.phoenixacces.apps.persistence.entities.module.livraison.FicheBonDeCommande;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MdleFicheComande implements Serializable {
    List<FicheBonDeCommande> attente;
    List<FicheBonDeCommande> valide;
    List<FicheBonDeCommande> traite;
    List<FicheBonDeCommande> rejete;
}
