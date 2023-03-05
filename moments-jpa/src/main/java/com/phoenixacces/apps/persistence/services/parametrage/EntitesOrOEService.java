package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.EntitesOrOE;
import com.phoenixacces.apps.persistence.repositories.parametrage.EntitesOrOERepository;
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
public class EntitesOrOEService {

    private final EntitesOrOERepository gareRoutiereRepository;

    @Autowired
    public EntitesOrOEService(EntitesOrOERepository gareRoutiereRepository) {
        this.gareRoutiereRepository = gareRoutiereRepository;
    }

    public List<EntitesOrOE> findAlls() {
        return gareRoutiereRepository.findAll();
    }


    public List<EntitesOrOE> findAll() {
        return gareRoutiereRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }


    public List<EntitesOrOE> findAll(Entreprises compagnieRoutiere) {
        return gareRoutiereRepository.findAllByActiveAndCompagnie(true, compagnieRoutiere).orElseGet(ArrayList::new);
    }

    public EntitesOrOE findOne(Entreprises compagnieRoutiere, String gare) {
        return gareRoutiereRepository.findByCompagnieAndActiveAndGareRoutiere(compagnieRoutiere, true, gare).orElseGet(() -> {
            return null;
        });
    }

    public EntitesOrOE findById(Long id) {
        return gareRoutiereRepository.findById(id).orElseGet(() -> {
            return null;
        });
    }

    public EntitesOrOE create(EntitesOrOE input) throws Exception {
        return gareRoutiereRepository.findByCompagnieAndActiveAndGareRoutiere(input.getCompagnie(), true, input.getGareRoutiere().toUpperCase()).orElseGet(() -> {
            EntitesOrOE a = new EntitesOrOE();
            a.setGareRoutiere(input.getGareRoutiere().toUpperCase());
            a.setContactResponsableGareRoutiere(input.getContactResponsableGareRoutiere().toUpperCase());
            a.setNomresponsable(input.getNomresponsable().toUpperCase());
            a.setSiteGeoGareRoutiere(input.getSiteGeoGareRoutiere().toUpperCase());
            a.setCompagnie(input.getCompagnie());
            a.setActive(true);
            a.setCreation(Instant.now());
            a.setLastUpdate(Instant.now());
            a.setContact(input.getContact());
            a.setFax(input.getFax());
            return gareRoutiereRepository.save(a);
        });
    }


    public EntitesOrOE update(EntitesOrOE c) throws Exception {
        gareRoutiereRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {
            /*b.setGareRoutiere(c.getGareRoutiere().toUpperCase());
            b.setContactResponsableGareRoutiere(c.getContactResponsableGareRoutiere().toUpperCase());
            b.setNomresponsable(c.getNomresponsable().toUpperCase());
            b.setSiteGeoGareRoutiere(c.getSiteGeoGareRoutiere().toUpperCase());
            b.setContact(c.getContact());
            b.setFax(c.getFax());
            b.setCompagnie(c.getCompagnie());*/
            c.setLastUpdate(Instant.now());
            gareRoutiereRepository.save(c);
        });
        return c;
    }

    public void disable(long id) throws Exception {
        gareRoutiereRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            gareRoutiereRepository.save(model);
        });
    }

    public void enabled(long id) throws Exception {
        gareRoutiereRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            gareRoutiereRepository.save(model);
        });
    }
}
