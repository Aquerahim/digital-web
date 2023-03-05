package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.TypeSouscrivant;
import com.phoenixacces.apps.persistence.repositories.parametrage.EntreprisesRepository;
import com.phoenixacces.apps.persistence.repositories.parametrage.TypeSouscrivantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class EntrepriseService {

    private final EntreprisesRepository compagnieRoutiereRepository;
    private final TypeSouscrivantRepository typeSouscrivantRepository;

    @Autowired
    public EntrepriseService(EntreprisesRepository compagnieRoutiereRepository,
                             TypeSouscrivantRepository typeSouscrivantRepository) {
        this.compagnieRoutiereRepository = compagnieRoutiereRepository;
        this.typeSouscrivantRepository = typeSouscrivantRepository;
    }


    public List<Entreprises> findAll() {
        return compagnieRoutiereRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }


    public List<Entreprises> findAll(boolean active) {
        return compagnieRoutiereRepository.findAllByActive(active).orElseGet(ArrayList::new);
    }


    public List<Entreprises> findAll(TypeSouscrivant typeSouscrivant) {
        return compagnieRoutiereRepository.findAllByActiveAndTypeSouscrivant(true, typeSouscrivant).orElseGet(ArrayList::new);
    }


    public Entreprises findOneByComapgnie(String compagnie) {
        return compagnieRoutiereRepository.findByCompagnieAndActive(compagnie.toUpperCase(), true).orElseGet(() -> {
            return null;
        });
    }


    public Entreprises findOneByRCCM(String rccm) {
        return compagnieRoutiereRepository.findByRccmAndActive(rccm, true).orElseGet(() -> {
            return null;
        });
    }


    public Entreprises findOne(Long Id) {
        return compagnieRoutiereRepository.findById(Id).orElseGet(() -> {
            return null;
        });
    }


    public Entreprises create(Entreprises e) {

        return compagnieRoutiereRepository.findByRccmAndCompagnieAndActive(e.getRccm(), e.getCompagnie(),true).orElseGet(() -> {

            LocalDate rappel = e.getDateFinContrat().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            e.setActive(true);

            e.setCreation(Instant.now());

            e.setLastUpdate(Instant.now());

            e.setLastUpdate(Instant.now());

            e.setDateRappelFinContrat(rappel.minusDays(5));

            e.setNotificateur(0L);

            /*
            e.setTypeSouscrivant(typeSouscrivantRepository.getOne(1L));

            /e.setTauxCommLivreur(e.getTauxCommLivreur()); //20%*/
            return compagnieRoutiereRepository.save(e);
        });
    }


    public Entreprises update(Entreprises model) throws Exception {

        compagnieRoutiereRepository.findByIdAndActive(model.getId(), true).ifPresent(b -> {

            /*b.setCompagnie(model.getCompagnie());
            b.setAbbrev(model.getAbbrev());
            b.setAdresse(model.getAdresse());
            b.setBp(model.getBp());
            b.setEmail(model.getEmail());
            b.setRccm(model.getRccm());
            b.setWeb(model.getWeb());
            b.setContact(model.getContact());
            b.setResponsable(model.getResponsable().toUpperCase());
            b.setContactResponsable(model.getContactResponsable());*/

            model.setLastUpdate(Instant.now());

            model.setVersion(model.getVersion() + 1);

            compagnieRoutiereRepository.save(model);
        });

        return model;
    }


    public void disable(long id) throws Exception {
        compagnieRoutiereRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            model.setVersion(model.getVersion() + 1);
            model.setLastUpdate(Instant.now());
            compagnieRoutiereRepository.save(model);
        });
    }


    public void actionCollaboration(Entreprises model) throws Exception {
        compagnieRoutiereRepository.findById(model.getId()).ifPresent(action -> {
            action.setMotifSuspensionCollaboration(model.getMotifSuspensionCollaboration());
            action.setDateFinContrat(model.getDateFinContrat());
            action.setActive(false);
            action.setVersion(action.getVersion() + 1);
            action.setLastUpdate(Instant.now());
            compagnieRoutiereRepository.save(action);
        });
    }


    public void enabled(long id) throws Exception {
        compagnieRoutiereRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            model.setVersion(model.getVersion() + 1);
            model.setLastUpdate(Instant.now());
            compagnieRoutiereRepository.save(model);
        });
    }
}
