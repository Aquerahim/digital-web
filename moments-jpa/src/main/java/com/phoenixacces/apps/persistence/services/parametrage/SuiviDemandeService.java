package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.module.ServiceCourrier;
import com.phoenixacces.apps.persistence.entities.parametrage.SuiviDemande;
import com.phoenixacces.apps.persistence.entities.parametrage.TypeContrat;
import com.phoenixacces.apps.persistence.repositories.parametrage.SuiviDemandeRepository;
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
public class SuiviDemandeService {

    private final SuiviDemandeRepository suiviDemandeRepository;

    @Autowired
    public SuiviDemandeService(SuiviDemandeRepository suiviDemandeRepository) {
        this.suiviDemandeRepository = suiviDemandeRepository;
    }

    public List<SuiviDemande> findAlls() {
        return suiviDemandeRepository.findAll();
    }

    public List<SuiviDemande> findAll() {
        return suiviDemandeRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public SuiviDemande findOne(String suivi) {
        return suiviDemandeRepository.findBySuiviAndActive(suivi, true).orElseGet(() -> null);
    }

    public SuiviDemande findOne(Long id) {
        return suiviDemandeRepository.findById(id).orElseGet(() -> null);
    }

    public SuiviDemande create(SuiviDemande input) throws Exception {
        return suiviDemandeRepository.findBySuiviAndActive(input.getSuivi().toUpperCase(), true).orElseGet(() -> {
            SuiviDemande a = new SuiviDemande();
            a.setSuivi(input.getSuivi().toUpperCase());
            a.setActive(true);
            a.setCreation(Instant.now());
            a.setLastUpdate(Instant.now());
            return suiviDemandeRepository.save(a);
        });
    }

    public SuiviDemande update(SuiviDemande request) {
        if (request.getId() != null) {
            request.setLastUpdate(Instant.now());
            return suiviDemandeRepository.save(request);
        }
        return request;
    }


    public void disable(long id) throws Exception {
        suiviDemandeRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            suiviDemandeRepository.save(model);
        });
    }

    public void enable(long id) throws Exception {
        suiviDemandeRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            suiviDemandeRepository.save(model);
        });
    }

    public List<SuiviDemande> demandeDisponible(Long id) {
        return suiviDemandeRepository.demandeDisponible(id).orElseGet(ArrayList::new);
    }



    public List<SuiviDemande> findAll(Long id, String mdle) {
        return suiviDemandeRepository.findAllByActiveAndMdle(id, mdle, true).orElseGet(ArrayList::new);
    }
}
