package com.phoenixacces.apps.persistence.services.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.NotificationEnMasse;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.module.assurance.NotificationEnMasseRepository;
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
public class NotificationEnMasseService {

    private final NotificationEnMasseRepository repository;

    @Autowired
    public NotificationEnMasseService(NotificationEnMasseRepository repository) {
        this.repository = repository;
    }


    public List<NotificationEnMasse> findAlls() {

        return repository.findAll();
    }


    public List<NotificationEnMasse> findAll() {

        return repository.findAllByActiveOrderByIdDesc(true).orElseGet(ArrayList::new);
    }


    public List<NotificationEnMasse> findAll(Entreprises entreprises) {

        return repository.findAllByActiveAndEntreprisesOrderByIdDesc(true, entreprises).orElseGet(ArrayList::new);
    }


    public NotificationEnMasse findOne(String name, Entreprises entreprises) {

        return repository.findByNomCampagneAndActiveAndEntreprisesOrderByIdDesc(name, true, entreprises).orElseGet(() -> null);
    }


    public NotificationEnMasse findByOrdre(String nomCapagne) {

        return repository.findByNomCampagneAndActive(nomCapagne, true).orElseGet(() -> null);
    }


    public NotificationEnMasse findById(Long id) {

        return repository.findById(id).orElseGet(() -> null);
    }


    public NotificationEnMasse create(NotificationEnMasse input) throws Exception {

        return repository.findByNomCampagneAndActiveAndEntreprisesOrderByIdDesc(input.getNomCampagne().toUpperCase(), true, input.getEntreprises()).orElseGet(() -> {

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            return repository.save(input);
        });
    }


    public NotificationEnMasse update(NotificationEnMasse c) throws Exception {

        repository.findByIdAndActive(c.getId(), true).ifPresent(b -> {

            c.setLastUpdate(Instant.now());

            repository.save(c);
        });

        return c;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        repository.findById(id).ifPresent(model -> {

            model.setActive(active);

            repository.save(model);
        });
    }
}
