package com.phoenixacces.apps.persistence.services.module.assurance;

import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.assurance.PorteFeuilleClient;
import com.phoenixacces.apps.persistence.entities.module.assurance.SendMessage;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.module.assurance.SendMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class SendMessageService {

    private final SendMessageRepository sendMessageRepository;


    @Autowired
    public SendMessageService(
                SendMessageRepository sendMessageRepository) {
        this.sendMessageRepository  = sendMessageRepository;
    }

    public List<SendMessage> findAll() {

        return sendMessageRepository.findAll();
    }

    public List<SendMessage> findAll(PorteFeuilleClient porteFeuilleClient) {

        return sendMessageRepository.findAllByPorteFeuilleClientOrderByIdDesc(porteFeuilleClient).orElseGet(ArrayList::new);
    }


    public List<SendMessage> findAll(Entreprises entreprises) {

        return sendMessageRepository.findAllByActiveAndEntreprises(true, entreprises).orElseGet(ArrayList::new);
    }


    public List<SendMessage> findAll(Entreprises entreprises, boolean active) {

        return sendMessageRepository.findAllByActiveAndEntreprises(active, entreprises).orElseGet(ArrayList::new);
    }


    public List<SendMessage> findAll(boolean active, Profile profile, EtatLecture statut) {

        return sendMessageRepository.findAllByActiveAndProfileAndStatutOrderByIdDesc(active, profile, statut).orElseGet(ArrayList::new);
    }


    public List<SendMessage> findAll(boolean active, Entreprises entreprises, EtatLecture etatLecture) {

        return sendMessageRepository.findAllByActiveAndEntreprisesAndStatutOrderByIdDesc(active, entreprises, etatLecture).orElseGet(ArrayList::new);
    }


    public List<SendMessage> findAll(EtatLecture statut) {

        return sendMessageRepository.findAllByActiveAndStatut(true, statut).orElseGet(ArrayList::new);
    }


    public SendMessage create(SendMessage input) {

        input.setActive(true);

        input.setCreation(Instant.now());

        input.setLastUpdate(Instant.now());

        return sendMessageRepository.save(input);
    }


    public SendMessage update(SendMessage c) throws Exception {

        sendMessageRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {

            c.setLastUpdate(Instant.now());

            sendMessageRepository.save(c);
        });

        return c;
    }


    public SendMessage findOne(Long id, EtatLecture etatLecture) {

        return sendMessageRepository.findByIdAndActiveAndStatut(id, true, etatLecture).orElseGet(() -> null);
    }



}
