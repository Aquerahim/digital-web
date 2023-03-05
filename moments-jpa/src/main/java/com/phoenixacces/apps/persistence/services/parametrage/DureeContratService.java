package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.DureeContrat;
import com.phoenixacces.apps.persistence.entities.parametrage.TypeSouscrivant;
import com.phoenixacces.apps.persistence.repositories.parametrage.DureeContratRepository;
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
public class DureeContratService {

    private final DureeContratRepository dureeContratRepository;

    @Autowired
    public DureeContratService(DureeContratRepository dureeContratRepository) {
        this.dureeContratRepository = dureeContratRepository;
    }

    public List<DureeContrat> findAlls() {
        return dureeContratRepository.findAll();
    }


    public List<DureeContrat> findAll() {
        return dureeContratRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public DureeContrat findOne(String name) {
        return dureeContratRepository.findByDureeContratAndActive(name, true).orElseGet(() -> {
            return null;
        });
    }

    public DureeContrat findById(Long id) {
        return dureeContratRepository.findById(id).orElseGet(() -> {
            return null;
        });
    }

    public DureeContrat create(DureeContrat input) throws Exception {
        return dureeContratRepository.findByDureeAndActive(input.getDuree(), true).orElseGet(() -> {
            DureeContrat a = new DureeContrat();
            a.setDuree(input.getDuree());
            a.setDureeContrat(input.getDuree()+" MOIS");
            a.setActive(true);
            a.setCreation(Instant.now());
            a.setLastUpdate(Instant.now());
            return dureeContratRepository.save(a);
        });
    }


    public DureeContrat update(DureeContrat c) throws Exception {
        dureeContratRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {
            //b.setTypesouscrivant(c.getTypesouscrivant().toUpperCase());
            c.setLastUpdate(Instant.now());
            dureeContratRepository.save(c);
        });
        return c;
    }

    public void disable(long id) throws Exception {
        dureeContratRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            dureeContratRepository.save(model);
        });
    }

    public void enable(long id) throws Exception {
        dureeContratRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            dureeContratRepository.save(model);
        });
    }
}
