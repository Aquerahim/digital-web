package com.phoenixacces.apps.persistence.services.module.assurance;

import com.phoenixacces.apps.persistence.entities.module.assurance.TypeClient;
import com.phoenixacces.apps.persistence.entities.service.TypeMessage;
import com.phoenixacces.apps.persistence.repositories.module.assurance.TypeClientRepository;
import com.phoenixacces.apps.persistence.repositories.parametrage.TypeMessageRepository;
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
public class TypeClientService {

    private final TypeClientRepository typeClientRepository;

    @Autowired
    public TypeClientService(TypeClientRepository typeMessageRepository) {
        this.typeClientRepository = typeMessageRepository;
    }

    public List<TypeClient> findAlls() {

        return typeClientRepository.findAll();
    }


    public List<TypeClient> findAll() {

        return typeClientRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }

    public TypeClient findOne(String name) {

        return typeClientRepository.findByTypeAndActive(name, true).orElseGet(() -> null);
    }

    public TypeClient findById(Long id) {

        return typeClientRepository.findById(id).orElseGet(() -> null);
    }

    public TypeClient create(TypeClient input) throws Exception {

        return typeClientRepository.findByTypeAndActive(input.getType().toUpperCase(), true).orElseGet(() -> {

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            return typeClientRepository.save(input);
        });
    }


    public TypeClient update(TypeClient c) throws Exception {

        typeClientRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {

            c.setLastUpdate(Instant.now());

            typeClientRepository.save(c);
        });

        return c;
    }

    public void actionRequest(long id, boolean active) throws Exception {

        typeClientRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            typeClientRepository.save(model);
        });
    }
}
