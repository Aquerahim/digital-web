package com.phoenixacces.apps.persistence.services.module;

import com.phoenixacces.apps.persistence.entities.module.Colis;
import com.phoenixacces.apps.persistence.repositories.module.ColisRepository;
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
public class ColisService {

    private final ColisRepository colisRepository;

    @Autowired
    public ColisService(ColisRepository colisRepository) {
        this.colisRepository = colisRepository;
    }


    public List<Colis> findAll() {
        return colisRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public List<Colis> findAll(String colisNumber) {
        return colisRepository.findAllByColisNumberAndActive(colisNumber, true).orElseGet(ArrayList::new);
    }

    public Colis findOne(String colisNumber) {
        return colisRepository.findByColisNumberAndActive(colisNumber, true).orElseGet(() -> null);
    }


    public Colis create(Colis input) throws Exception {
        return colisRepository.findByColisNumberAndActiveAndNatureColisAndDesignationColis(input.getColisNumber().toUpperCase(),
                true, input.getNatureColis(), input.getDesignationColis()).orElseGet(() -> {
            Colis a = new Colis();
            a.setColisNumber(input.getColisNumber().toUpperCase());
            a.setNatureColis(input.getNatureColis());
            a.setActive(true);
            a.setDesignationColis(input.getDesignationColis());
            a.setCreation(Instant.now());
            a.setLastUpdate(Instant.now());
            return colisRepository.save(a);
        });
    }


    public Colis update(Colis request) {
        if (request.getId() != null) {
            request.setLastUpdate(Instant.now());
            request.setVersion(request.getVersion()+1);
            return colisRepository.save(request);
        }
        return request;
    }

    public void disable(long id) throws Exception {
        colisRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            colisRepository.save(model);
        });
    }

    public void enable(long id) throws Exception {
        colisRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            colisRepository.save(model);
        });
    }
}
