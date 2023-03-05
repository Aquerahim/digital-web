package com.phoenixacces.apps.persistence.services.module.livraison;


import com.phoenixacces.apps.enumerations.TypeClientLivraison;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.livraison.PartenaireAffaire;
import com.phoenixacces.apps.persistence.repositories.module.livraison.MesClientsRepository;
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
public class MesClientsService {

    private final MesClientsRepository mesClientsRepository;

    @Autowired
    public MesClientsService(MesClientsRepository mesClientsRepository) {
        this.mesClientsRepository = mesClientsRepository;
    }


    public List<PartenaireAffaire> findAll() {

        return mesClientsRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }


    public List<PartenaireAffaire> findAll(Profile profile) {

        return mesClientsRepository.findAllByActiveAndProfile(true, profile).orElseGet(ArrayList::new);
    }

    public PartenaireAffaire findOne(Long numOrdre) {

        return mesClientsRepository.findByOrdreAndActive(numOrdre, true).orElseGet(() -> null);
    }

    public PartenaireAffaire findOne(String liaison) {

        return mesClientsRepository.findByLiaisonAndActive(liaison, true).orElseGet(() -> null);
    }

    public PartenaireAffaire findOneById(Long clientid) {

        return mesClientsRepository.findByIdAndActive(clientid, true).orElseGet(() -> null);
    }

    public PartenaireAffaire findOne(String nomComplet, TypeClientLivraison type) {

        return mesClientsRepository.findByNomCompletAndActiveAndTypeClient(nomComplet, true, type).orElseGet(() -> null);
    }


    public PartenaireAffaire create(PartenaireAffaire e) throws Exception {

        return mesClientsRepository.findByNomCompletAndActiveAndTypeClient(e.getNomComplet().toUpperCase(), true, e.getTypeClient()).orElseGet(() -> {

            e.setActive(true);

            e.setCreation(Instant.now());

            e.setLastUpdate(Instant.now());

            return mesClientsRepository.save(e);

        });
    }


    public PartenaireAffaire update(PartenaireAffaire request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            request.setVersion(request.getVersion()+1);

            return mesClientsRepository.save(request);

        }

        return request;
    }

    public void disable(long id) throws Exception {

        mesClientsRepository.findById(id).ifPresent(model -> {

            model.setActive(false);

            mesClientsRepository.save(model);

        });
    }

    public void enable(long id) throws Exception {

        mesClientsRepository.findById(id).ifPresent(model -> {

            model.setActive(true);

            mesClientsRepository.save(model);

        });
    }
}
