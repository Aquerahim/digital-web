package com.phoenixacces.apps.persistence.services.module.livraison;

import com.phoenixacces.apps.enumerations.StatutLivraison;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livraisons;
import com.phoenixacces.apps.persistence.repositories.module.livraison.LivraisonsRepository;
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
public class LivraisonsService {

    private final LivraisonsRepository repository;

    @Autowired
    public LivraisonsService(LivraisonsRepository repository) {
        this.repository = repository;
    }


    public List<Livraisons> findAll() {

        return repository.findAllByActive(true).orElseGet(ArrayList::new);
    }


    public List<Livraisons> findAlls() {

        return repository.findAll();
    }


    public List<Livraisons> findAllByProfile(Profile profile) {

        return repository.findAllByActiveAndProfileOrderByIdDesc(true, profile).orElseGet(ArrayList::new);
        //return repository.findAllByActiveAndProfile(true, profile).orElseGet(ArrayList::new);
    }



    public List<Livraisons> findAllLivraionByProfile(Profile profile, StatutLivraison statut) {

        return repository.findAllByActiveAndProfileAndStatutLivraisonOrderByIdDesc(true, profile, statut).orElseGet(ArrayList::new);
    }


    public List<Livraisons> findAllByProfile(Profile profile, String notif) {

        return repository.findAllByActiveAndProfileAndNotifClientOrderByIdDesc(true, profile, notif).orElseGet(ArrayList::new);
    }


    public List<Livraisons> findAllByProfile(Profile profile, String notif, StatutLivraison statutLivraison) {

        return repository.findAllByActiveAndProfileAndNotifClientAndStatutLivraisonOrderByIdDesc(true, profile, notif, statutLivraison).orElseGet(ArrayList::new);
    }


    public Livraisons findOne(String reference) {

        return repository.findByReferenceAndActive(reference, true).orElseGet(() -> null);
    }


    public Livraisons findOne(Long ordre, String reference) {

        return repository.findByOrdreAndActiveAndReference(ordre, true, reference).orElseGet(() -> null);
    }


    public Livraisons findOneByOrdre(Long ordre) {

        return repository.findByOrdreAndActive(ordre, true).orElseGet(() -> null);
    }


    public Livraisons findOne(Long id) {

        return repository.findById(id).orElseGet(() -> null);
    }


    public Livraisons create(Livraisons input) throws Exception {

        return repository.findByReferenceAndActive(input.getReference(), true).orElseGet(() -> {

            input.setMessageRemerciement(0);

            input.setStatutLivraison(StatutLivraison.EN_ATTENTE);

            input.setCommissionLivreur(input.getPrixLivraison() * input.getLivreur().getTauxComm());

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            input.setNotifs("-");

            input.setVersion(0);

            return repository.save(input);

        });
    }


    public Livraisons update(Livraisons request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

            request.setVersion(request.getVersion() + 1);

            return repository.save(request);
        }

        return request;
    }


    public void disable(long id) throws Exception {

        repository.findById(id).ifPresent(model -> {

            model.setActive(false);

            repository.save(model);

        });
    }


    public void enable(long id) throws Exception {

        repository.findById(id).ifPresent(model -> {

            model.setActive(true);

            repository.save(model);
        });
    }
}
