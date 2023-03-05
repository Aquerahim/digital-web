package com.phoenixacces.apps.persistence.models;

import com.phoenixacces.apps.persistence.entities.module.assurance.SendMessage;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livraisons;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class StatistiqueAssurance implements Serializable {

    private double nbrSmsProgramme;

    private double nbrSmsDejaEnvoye;

    private double nbreClientPorteFeuille;

    private double coutConsommation;

    private List<SendMessageDto> sendMessageList;
}
