package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.NatureDepot;
import com.phoenixacces.apps.persistence.repositories.module.depot.NatureDepotRepository;
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
public class NatureDepotService {

    private final NatureDepotRepository natureDepotRepository;

    @Autowired
    public NatureDepotService(NatureDepotRepository natureDepotRepository) {
        this.natureDepotRepository = natureDepotRepository;
    }


    public List<NatureDepot> findAll() {

        return natureDepotRepository.findAll();
    }

    public List<NatureDepot> findAll(Boolean aBoolean) {

        return natureDepotRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public NatureDepot findOne(String name, Boolean aBoolean) {

        return natureDepotRepository.findByNatMouvtAndActive(name, aBoolean).orElseGet(() -> null);
    }

    public NatureDepot findByOrdre(Long ordre) {

        return natureDepotRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public NatureDepot findById(Long id) {

        return natureDepotRepository.findById(id).orElseGet(() -> null);
    }

    public NatureDepot create(NatureDepot item) throws Exception {

        return natureDepotRepository.findByNatMouvtAndActive(item.getNatMouvt(), true).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return natureDepotRepository.save(item);
        });
    }


    public NatureDepot update(NatureDepot donne) throws Exception {

        natureDepotRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            natureDepotRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        natureDepotRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            natureDepotRepository.save(model);
        });
    }
}
