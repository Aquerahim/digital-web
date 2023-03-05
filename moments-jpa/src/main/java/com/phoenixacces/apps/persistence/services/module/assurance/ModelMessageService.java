package com.phoenixacces.apps.persistence.services.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.ModelMessage;
import com.phoenixacces.apps.persistence.entities.module.assurance.TypeClient;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.service.TypeMessage;
import com.phoenixacces.apps.persistence.repositories.module.assurance.ModelMessageRepository;
import com.phoenixacces.apps.persistence.repositories.parametrage.BirthdayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ModelMessageService {

    private final ModelMessageRepository modelMessageRepository;

    @Autowired
    public ModelMessageService(ModelMessageRepository modelMessageRepository) {
        
        this.modelMessageRepository = modelMessageRepository;
    }

    public List<ModelMessage> findAlls() {

        return modelMessageRepository.findAll();
    }


    public List<ModelMessage> findAll() {

        return modelMessageRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public ModelMessage findOne(TypeMessage typeMessage, Entreprises entreprises, String libelle) {

        return modelMessageRepository.findByTypeMessageAndEntreprisesAndActiveAndLibelle(typeMessage, entreprises,true, libelle.toUpperCase()).orElseGet(() -> null);
    }

    public ModelMessage findOne(TypeMessage typeMessage, Entreprises entreprises) {

        return modelMessageRepository.findByTypeMessageAndEntreprisesAndActive(typeMessage, entreprises,true).orElseGet(() -> null);
    }

    public ModelMessage findById(Long id) {

        return modelMessageRepository.findById(id).orElseGet(() -> null);
    }

    public ModelMessage create(ModelMessage input) throws Exception {

        return modelMessageRepository.findByTypeMessageAndEntreprisesAndActive(input.getTypeMessage(), input.getEntreprises(), true).orElseGet(() -> {

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            return modelMessageRepository.save(input);
        });
    }


    public ModelMessage update(ModelMessage c) throws Exception {

        modelMessageRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {

            c.setLastUpdate(Instant.now());

            modelMessageRepository.save(c);
        });

        return c;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        modelMessageRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            modelMessageRepository.save(model);
        });
    }



    public ModelMessage findOne(Entreprises entreprises, String libelle) {

        return modelMessageRepository.findByEntreprisesAndActiveAndLibelle(entreprises,true, libelle.toUpperCase()).orElseGet(() -> null);
    }
}
