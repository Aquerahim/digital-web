package com.phoenixacces.apps.persistence.models;

import com.phoenixacces.apps.persistence.entities.module.assurance.PorteFeuilleClient;
import com.phoenixacces.apps.persistence.entities.module.assurance.SendMessage;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livraisons;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
public class ModelPorteFeuille implements Serializable {

    private int messageAnnule;

    private int messageEnvoye;

    private int messageNonEnvoye;

    private int messageEnAttente;

    private List<SendMessage> sendMessageList;

    private PorteFeuilleClient porteFeuilleClient;
}
