package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.Civilite;
import com.phoenixacces.apps.persistence.repositories.parametrage.CiviliteRepository;
import com.phoenixacces.apps.persistence.repositories.parametrage.TypeMessageRepository;
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
public class CiviliteService {

    private final CiviliteRepository civiliteRepository;

    @Autowired
    public CiviliteService(CiviliteRepository civiliteRepository) {
        this.civiliteRepository = civiliteRepository;
    }


    public List<Civilite> findAll() {
        return civiliteRepository.findAll();
    }


    public Civilite findOne(String name) {
        return civiliteRepository.findByCiviliteAndActive(name, true).orElseGet(() -> null);
    }

    public Civilite findById(Long id) {
        return civiliteRepository.findById(id).orElseGet(() -> null);
    }

    public Civilite create(Civilite input) throws Exception {

        return civiliteRepository.findByCiviliteAndActive(input.getCivilite().toUpperCase(), true).orElseGet(() -> {

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            return civiliteRepository.save(input);
        });
    }


    public Civilite update(Civilite c) throws Exception {

        civiliteRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {

            c.setLastUpdate(Instant.now());

            civiliteRepository.save(c);
        });
        return c;
    }

    public void disable(long id) {
        civiliteRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            civiliteRepository.save(model);
        });
    }

    public void enable(long id) {
        civiliteRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            civiliteRepository.save(model);
        });
    }

    public void actionRequest(long id, boolean active) throws Exception {

        civiliteRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            civiliteRepository.save(model);
        });
    }



}
