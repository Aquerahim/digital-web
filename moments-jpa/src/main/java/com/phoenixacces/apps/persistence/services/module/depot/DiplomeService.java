package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Diplome;
import com.phoenixacces.apps.persistence.entities.module.depot.MotifAvance;
import com.phoenixacces.apps.persistence.repositories.module.depot.DiplomeRepository;
import com.phoenixacces.apps.persistence.repositories.module.depot.MotifAvanceRepository;
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
public class DiplomeService {

    private final DiplomeRepository diplomeRepository;

    @Autowired
    public DiplomeService(DiplomeRepository diplomeRepository) {
        this.diplomeRepository = diplomeRepository;
    }


    public List<Diplome> findAll() {

        return diplomeRepository.findAll();
    }

    public List<Diplome> findAll(Boolean aBoolean) {

        return diplomeRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public Diplome findOne(String name, Boolean aBoolean) {

        return diplomeRepository.findByDiplomeAndActive(name, aBoolean).orElseGet(() -> null);
    }

    public Diplome findByOrdre(Long ordre) {

        return diplomeRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public Diplome findById(Long id) {

        return diplomeRepository.findById(id).orElseGet(() -> null);
    }

    public Diplome create(Diplome item) throws Exception {

        return diplomeRepository.findByDiplomeAndActive(item.getDiplome(), true).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return diplomeRepository.save(item);
        });
    }


    public Diplome update(Diplome donne) throws Exception {

        diplomeRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            diplomeRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        diplomeRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            diplomeRepository.save(model);
        });
    }
}
