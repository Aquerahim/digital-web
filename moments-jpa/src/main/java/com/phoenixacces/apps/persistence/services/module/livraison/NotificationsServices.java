package com.phoenixacces.apps.persistence.services.module.livraison;

import com.phoenixacces.apps.enumerations.StatutLivraison;
import com.phoenixacces.apps.persistence.entities.module.livraison.Notifications;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typezone.TypeZoneCouverture;
import com.phoenixacces.apps.persistence.repositories.module.livraison.NotificationsRepository;
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
public class NotificationsServices {

    private final NotificationsRepository repository;

    @Autowired
    public NotificationsServices(NotificationsRepository repository) {
        this.repository = repository;
    }


    public List<Notifications> findAlls() {

        return repository.findAll();
    }



    public List<Notifications> findAll() {

        return repository.findAllByActive(true).orElseGet(ArrayList::new);
    }


    public List<Notifications> findAll(StatutLivraison StatutEnvoi) {

        return repository.findAllByStatutEnvoiAndActive(StatutEnvoi, true).orElseGet(ArrayList::new);
    }


    public Notifications findOne(StatutLivraison StatutEnvoi) {
        return repository.findByStatutEnvoiAndActive(StatutEnvoi, true).orElseGet(() -> null);
    }


    public Notifications findOne(String ref, boolean envoi) {
        return repository.findByRefAndEnvoiAndActive(ref, envoi,true).orElseGet(() -> null);
    }


    public Notifications create(Notifications input) throws Exception {

        input.setStatutEnvoi(StatutLivraison.PENDING);

        input.setEnvoi(false);

        input.setActive(true);

        input.setCreation(Instant.now());

        input.setLastUpdate(Instant.now());

        return repository.save(input);
    }

    public Notifications update(Notifications request) {

        if (request.getId() != null) {

            request.setLastUpdate(Instant.now());

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
