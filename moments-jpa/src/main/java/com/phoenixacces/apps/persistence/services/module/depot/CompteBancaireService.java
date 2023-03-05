package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Banque;
import com.phoenixacces.apps.persistence.entities.module.depot.CompteBancaire;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.module.depot.BanqueRepository;
import com.phoenixacces.apps.persistence.repositories.module.depot.CompteBancaireRepository;
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
public class CompteBancaireService {

    private final CompteBancaireRepository compteBancaireRepository;

    @Autowired
    public CompteBancaireService(CompteBancaireRepository compteBancaireRepository) {
        this.compteBancaireRepository = compteBancaireRepository;
    }


    public List<CompteBancaire> findAll() {

        return compteBancaireRepository.findAll();
    }

    public List<CompteBancaire> findAll(Boolean aBoolean) {

        return compteBancaireRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }


    public List<CompteBancaire> findAll(Entreprises entreprises, Boolean aBoolean) {

        return compteBancaireRepository.findAllByActiveAndEntreprisesOrderByIdDesc(aBoolean, entreprises).orElseGet(ArrayList::new);
    }

    public CompteBancaire findOne(Banque banque, Entreprises entreprises, Boolean aBoolean) {

        return compteBancaireRepository.findByBanqueAndActiveAndEntreprisesOrderByIdDesc(banque, aBoolean, entreprises).orElseGet(() -> null);
    }

    public CompteBancaire findByOrdre(Long ordre) {

        return compteBancaireRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public CompteBancaire findById(Long id) {

        return compteBancaireRepository.findById(id).orElseGet(() -> null);
    }

    public CompteBancaire create(CompteBancaire item) throws Exception {

        return compteBancaireRepository.findByBanqueAndActiveAndEntreprisesOrderByIdDesc(item.getBanque(), true, item.getEntreprises()).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return compteBancaireRepository.save(item);
        });
    }


    public CompteBancaire update(CompteBancaire donne) throws Exception {

        compteBancaireRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            compteBancaireRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        compteBancaireRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            compteBancaireRepository.save(model);
        });
    }
}
