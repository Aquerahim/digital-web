package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Banque;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.module.depot.BanqueRepository;
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
public class BanqueService {

    private final BanqueRepository banqueRepository;

    @Autowired
    public BanqueService(BanqueRepository banqueRepository) {
        this.banqueRepository = banqueRepository;
    }


    public List<Banque> findAll() {

        return banqueRepository.findAll();
    }

    public List<Banque> findAll(Boolean aBoolean) {

        return banqueRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public Banque findOne(String name, Boolean aBoolean) {

        return banqueRepository.findByNomBanqueAndActive(name, aBoolean).orElseGet(() -> null);
    }

    public Banque findByOrdre(Long ordre) {

        return banqueRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public Banque findById(Long id) {

        return banqueRepository.findById(id).orElseGet(() -> null);
    }

    public Banque create(Banque item) throws Exception {

        return banqueRepository.findByNomBanqueAndActive(item.getNomBanque().toUpperCase(), true).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return banqueRepository.save(item);
        });
    }


    public Banque update(Banque donne) throws Exception {

        banqueRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            banqueRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        banqueRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            banqueRepository.save(model);
        });
    }
}
