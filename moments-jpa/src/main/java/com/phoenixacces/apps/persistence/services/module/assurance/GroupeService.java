package com.phoenixacces.apps.persistence.services.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.Groupe;
import com.phoenixacces.apps.persistence.entities.module.assurance.TypeClient;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.module.assurance.GroupeRepository;
import com.phoenixacces.apps.persistence.repositories.module.assurance.TypeClientRepository;
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
public class GroupeService {

    private final GroupeRepository groupeRepository;

    @Autowired
    public GroupeService(GroupeRepository groupeRepository) {
        this.groupeRepository = groupeRepository;
    }

    public List<Groupe> findAlls() {

        return groupeRepository.findAll();
    }


    public List<Groupe> findAll() {

        return groupeRepository.findAllByActiveOrderByIdDesc(true).orElseGet(ArrayList::new);
    }


    public List<Groupe> findAll(Entreprises entreprises) {

        return groupeRepository.findAllByActiveAndEntreprisesOrderByIdDesc(true, entreprises).orElseGet(ArrayList::new);
    }

    public Groupe findOne(String name, Entreprises entreprises) {

        return groupeRepository.findByNomGroupeAndActiveAndEntreprisesOrderByIdDesc(name, true, entreprises).orElseGet(() -> null);
    }

    public Groupe findByOrdre(String ordre) {

        return groupeRepository.findByOrdre(ordre).orElseGet(() -> null);
    }

    public Groupe findById(Long id) {

        return groupeRepository.findById(id).orElseGet(() -> null);
    }

    public Groupe create(Groupe input) throws Exception {

        return groupeRepository.findByNomGroupeAndActiveAndEntreprisesOrderByIdDesc(input.getNomGroupe().toUpperCase(), true, input.getEntreprises()).orElseGet(() -> {

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            return groupeRepository.save(input);
        });
    }


    public Groupe update(Groupe c) throws Exception {

        groupeRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {

            c.setLastUpdate(Instant.now());

            groupeRepository.save(c);
        });

        return c;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        groupeRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            groupeRepository.save(model);
        });
    }
}
