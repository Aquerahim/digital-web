package com.phoenixacces.apps.persistence.services.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.ParametrageDates;
import com.phoenixacces.apps.persistence.repositories.module.assurance.ParametrageDateRepository;
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
public class ParametrageDateService {

    private final ParametrageDateRepository parametrageDateRepository;

    @Autowired
    public ParametrageDateService(ParametrageDateRepository parametrageDateRepository) {
        this.parametrageDateRepository = parametrageDateRepository;
    }

    public List<ParametrageDates> findAlls() {

        return parametrageDateRepository.findAll();
    }


    public List<ParametrageDates> findAll() {

        return parametrageDateRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public ParametrageDates findOne(String dateDeb, String typeDateDeb) {

        return parametrageDateRepository.findByDateDebAndTypeDateDebAndActive(dateDeb, typeDateDeb,true).orElseGet(() -> null);
    }

    public ParametrageDates findOne(String typeDateDeb) {

        return parametrageDateRepository.findByTypeDateDebAndActive(typeDateDeb,true).orElseGet(() -> null);
    }

    public ParametrageDates findById(Long id) {

        return parametrageDateRepository.findById(id).orElseGet(() -> null);
    }

    public ParametrageDates create(ParametrageDates input) {

        return parametrageDateRepository.findByDateDebAndTypeDateDebAndActive(input.getDateDeb(), input.getTypeDateDeb().toLowerCase(), true).orElseGet(() -> {

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            return parametrageDateRepository.save(input);
        });
    }


    public ParametrageDates update(ParametrageDates c) {

        parametrageDateRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {

            c.setLastUpdate(Instant.now());

            parametrageDateRepository.save(c);
        });

        return c;
    }

    public void actionRequest(long id, boolean active)  {

        parametrageDateRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            parametrageDateRepository.save(model);
        });
    }
}
