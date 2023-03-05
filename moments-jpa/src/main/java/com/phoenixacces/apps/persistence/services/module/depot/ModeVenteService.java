package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Banque;
import com.phoenixacces.apps.persistence.entities.module.depot.ModeVente;
import com.phoenixacces.apps.persistence.repositories.module.depot.BanqueRepository;
import com.phoenixacces.apps.persistence.repositories.module.depot.ModeVenteRepository;
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
public class ModeVenteService {

    private final ModeVenteRepository modeVenteRepository;

    @Autowired
    public ModeVenteService(ModeVenteRepository modeVenteRepository) {
        this.modeVenteRepository = modeVenteRepository;
    }


    public List<ModeVente> findAll() {

        return modeVenteRepository.findAll();
    }

    public List<ModeVente> findAll(Boolean aBoolean) {

        return modeVenteRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public ModeVente findOne(String name, Boolean aBoolean) {

        return modeVenteRepository.findByModeVenteAndActive(name, aBoolean).orElseGet(() -> null);
    }

    public ModeVente findByOrdre(Long ordre) {

        return modeVenteRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public ModeVente findById(Long id) {

        return modeVenteRepository.findById(id).orElseGet(() -> null);
    }

    public ModeVente create(ModeVente item) throws Exception {

        return modeVenteRepository.findByModeVenteAndActive(item.getModeVente(), true).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return modeVenteRepository.save(item);
        });
    }


    public ModeVente update(ModeVente donne) throws Exception {

        modeVenteRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            modeVenteRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        modeVenteRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            modeVenteRepository.save(model);
        });
    }
}
