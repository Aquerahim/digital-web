package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.parametrage.PaysAutorise;
import com.phoenixacces.apps.persistence.repositories.parametrage.PaysAutoriseRepository;
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
public class PaysAutoriseService {

    private final PaysAutoriseRepository paysAutoriseRepository;

    @Autowired
    public PaysAutoriseService(PaysAutoriseRepository paysAutoriseRepository) {
        this.paysAutoriseRepository = paysAutoriseRepository;
    }

    public List<PaysAutorise> findAlls() {
        return paysAutoriseRepository.findAll();
    }

    public List<PaysAutorise> findAll() {
        return paysAutoriseRepository.findAllByActive(true).orElseGet(() -> {
            return new ArrayList<>();
        });
    }

    public PaysAutorise findOne(String paysautorise) {
        return paysAutoriseRepository.findByPaysautoriseAndActive(paysautorise, true).orElseGet(() -> {
            return null;
        });
    }

    public PaysAutorise findOne(Long id) {
        return paysAutoriseRepository.findById(id).orElseGet(() -> {
            return null;
        });
    }

    public PaysAutorise create(PaysAutorise input) throws Exception {
        return paysAutoriseRepository.findByPaysautoriseAndActive(input.getPaysautorise().toUpperCase(), true).orElseGet(() -> {
            PaysAutorise a = new PaysAutorise();
            a.setPaysautorise(input.getPaysautorise().toUpperCase());
            a.setIndicatif(input.getIndicatif().toUpperCase());
            a.setActive(true);
            a.setCreation(Instant.now());
            a.setLastUpdate(Instant.now());
            return paysAutoriseRepository.save(a);
        });
    }


    public PaysAutorise update(PaysAutorise upt) throws Exception {
        paysAutoriseRepository.findByIdAndActive(upt.getId(), true).ifPresent(b -> {
            //b.setPaysautorise(upt.getPaysautorise().toUpperCase());
            //b.setIndicatif(upt.getIndicatif());
            upt.setLastUpdate(Instant.now());
            paysAutoriseRepository.save(upt);
        });
        return upt;
    }

    public void disable(long id) throws Exception {
        paysAutoriseRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            paysAutoriseRepository.save(model);
        });
    }

    public void enable(long id) throws Exception {
        paysAutoriseRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            paysAutoriseRepository.save(model);
        });
    }
}
