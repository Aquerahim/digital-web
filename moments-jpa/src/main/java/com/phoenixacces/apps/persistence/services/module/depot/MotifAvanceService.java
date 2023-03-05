package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.MotifAvance;
import com.phoenixacces.apps.persistence.entities.module.depot.TypesPaiement;
import com.phoenixacces.apps.persistence.repositories.module.depot.MotifAvanceRepository;
import com.phoenixacces.apps.persistence.repositories.module.depot.TypesPaiementRepository;
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
public class MotifAvanceService {

    private final MotifAvanceRepository motifAvanceRepository;

    @Autowired
    public MotifAvanceService(MotifAvanceRepository motifAvanceRepository) {
        this.motifAvanceRepository = motifAvanceRepository;
    }


    public List<MotifAvance> findAll() {

        return motifAvanceRepository.findAll();
    }

    public List<MotifAvance> findAll(Boolean aBoolean) {

        return motifAvanceRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public MotifAvance findOne(String name, Boolean aBoolean) {

        return motifAvanceRepository.findByMotifAndActive(name, aBoolean).orElseGet(() -> null);
    }

    public MotifAvance findByOrdre(Long ordre) {

        return motifAvanceRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public MotifAvance findById(Long id) {

        return motifAvanceRepository.findById(id).orElseGet(() -> null);
    }

    public MotifAvance create(MotifAvance item) throws Exception {

        return motifAvanceRepository.findByMotifAndActive(item.getMotif(), true).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return motifAvanceRepository.save(item);
        });
    }


    public MotifAvance update(MotifAvance donne) throws Exception {

        motifAvanceRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            motifAvanceRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        motifAvanceRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            motifAvanceRepository.save(model);
        });
    }
}
