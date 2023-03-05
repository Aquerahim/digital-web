package com.phoenixacces.apps.persistence.services.module.depot;

import com.phoenixacces.apps.persistence.entities.module.depot.Fournisseur;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.module.depot.FournisseurRepository;
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
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    @Autowired
    public FournisseurService(FournisseurRepository fournisseurRepository) {
        this.fournisseurRepository = fournisseurRepository;
    }


    public List<Fournisseur> findAll() {

        return fournisseurRepository.findAll();
    }

    public List<Fournisseur> findAll(Boolean aBoolean) {

        return fournisseurRepository.findAllByActiveOrderByIdDesc(aBoolean).orElseGet(ArrayList::new);
    }

    public List<Fournisseur> findAll(Boolean aBoolean, Entreprises entreprises) {

        return fournisseurRepository.findAllByActiveAndEntreprisesOrderByIdDesc(aBoolean, entreprises).orElseGet(ArrayList::new);
    }


    public Fournisseur findOne(String name, Boolean aBoolean) {

        return fournisseurRepository.findByNomCompletAndActive(name, aBoolean).orElseGet(() -> null);
    }

    public Fournisseur findByOrdre(Long ordre) {

        return fournisseurRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public Fournisseur findById(Long id) {

        return fournisseurRepository.findById(id).orElseGet(() -> null);
    }

    public Fournisseur create(Fournisseur item) throws Exception {

        return fournisseurRepository.findByNomCompletAndActive(item.getNomComplet(), true).orElseGet(() -> {

            item.setActive(true);

            item.setCreation(Instant.now());

            item.setLastUpdate(Instant.now());

            return fournisseurRepository.save(item);
        });
    }


    public Fournisseur update(Fournisseur donne) throws Exception {

        fournisseurRepository.findByIdAndActive(donne.getId(), true).ifPresent(b -> {

            donne.setLastUpdate(Instant.now());

            fournisseurRepository.save(donne);
        });

        return donne;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        fournisseurRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            fournisseurRepository.save(model);
        });
    }
}
