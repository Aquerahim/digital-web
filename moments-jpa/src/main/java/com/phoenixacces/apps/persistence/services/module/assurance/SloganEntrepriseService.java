package com.phoenixacces.apps.persistence.services.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.SloganEntreprise;
import com.phoenixacces.apps.persistence.entities.module.assurance.TypeClient;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.repositories.module.assurance.SloganEntrepriseRepository;
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
public class SloganEntrepriseService {

    private final SloganEntrepriseRepository sloganEntrepriseRepository;

    @Autowired
    public SloganEntrepriseService(SloganEntrepriseRepository sloganEntrepriseRepository) {
        this.sloganEntrepriseRepository = sloganEntrepriseRepository;
    }

    public List<SloganEntreprise> findAlls() {

        return sloganEntrepriseRepository.findAll();
    }


    public List<SloganEntreprise> findAll() {

        return sloganEntrepriseRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public SloganEntreprise findOne(Entreprises entreprises) {

        return sloganEntrepriseRepository.findByEntreprisesAndActive(entreprises,true).orElseGet(() -> null);
    }

    public SloganEntreprise findById(Long id) {

        return sloganEntrepriseRepository.findById(id).orElseGet(() -> null);
    }

    public SloganEntreprise create(SloganEntreprise input) throws Exception {

        return sloganEntrepriseRepository.findByEntreprisesAndActive(input.getEntreprises(), true).orElseGet(() -> {

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            return sloganEntrepriseRepository.save(input);
        });
    }


    public SloganEntreprise update(SloganEntreprise c) throws Exception {

        sloganEntrepriseRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {

            c.setLastUpdate(Instant.now());

            sloganEntrepriseRepository.save(c);
        });

        return c;
    }


    public void actionRequest(long id, boolean active) throws Exception {

        sloganEntrepriseRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            sloganEntrepriseRepository.save(model);
        });
    }
}
