package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.OffreSmsEntreprise;
import com.phoenixacces.apps.persistence.entities.parametrage.RechargementCompteSms;
import com.phoenixacces.apps.persistence.repositories.parametrage.OffreSmsEntrepriseRepository;
import com.phoenixacces.apps.persistence.repositories.parametrage.RechargementCompteSmsRepository;
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
public class OffreEntrepriseService {

    private final OffreSmsEntrepriseRepository offreSmsEntrepriseRepository;

    @Autowired
    public OffreEntrepriseService(
            OffreSmsEntrepriseRepository offreSmsEntrepriseRepository
    ) {
        this.offreSmsEntrepriseRepository        = offreSmsEntrepriseRepository;
    }


    public List<OffreSmsEntreprise> findAll() {
        return offreSmsEntrepriseRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public OffreSmsEntreprise findOne(String ordre) {
        return offreSmsEntrepriseRepository.findAllByActiveAndOrdre(true, ordre).orElseGet(() -> null);
    }

    public OffreSmsEntreprise findOne(String nomFormule, String typeFormule) {
        return offreSmsEntrepriseRepository.findAllByActiveAndNomFormuleAndTypeFormule(true, nomFormule, typeFormule).orElseGet(() -> null);
    }

    public OffreSmsEntreprise findOne(Long id) {
        return offreSmsEntrepriseRepository.findByIdAndActive(id, true).orElseGet(() -> null);
    }

    public OffreSmsEntreprise create(OffreSmsEntreprise input) {

        input.setActive(true);

        input.setCreation(Instant.now());

        input.setLastUpdate(input.getCreation());

        return offreSmsEntrepriseRepository.save(input);
    }


    public OffreSmsEntreprise update(OffreSmsEntreprise c) {

        c.setLastUpdate(Instant.now());

        return offreSmsEntrepriseRepository.save(c);
    }


    public void disable(long id) {
        offreSmsEntrepriseRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            offreSmsEntrepriseRepository.save(model);
        });
    }

    public void enable(long id) {
        offreSmsEntrepriseRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            offreSmsEntrepriseRepository.save(model);
        });
    }
}
