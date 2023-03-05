package com.phoenixacces.apps.persistence.services.parametrage;

import com.phoenixacces.apps.persistence.entities.service.TypeMessage;
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
public class TypeMessageService {

    private final TypeMessageRepository typeMessageRepository;

    @Autowired
    public TypeMessageService(TypeMessageRepository typeMessageRepository) {
        this.typeMessageRepository = typeMessageRepository;
    }

    public List<TypeMessage> findAlls() {
        return typeMessageRepository.findAll();
    }


    public List<TypeMessage> findAll() {
        return typeMessageRepository.findAllByActive(true).orElseGet(ArrayList::new);
    }


    public List<TypeMessage> findAll(String module) {
        return typeMessageRepository.findAllByActiveAndModule(true, module).orElseGet(ArrayList::new);
    }

    public TypeMessage findOne(String name) {
        return typeMessageRepository.findByTypemessgaeAndActive(name, true).orElseGet(() -> {
            return null;
        });
    }

    public TypeMessage findById(Long id) {
        return typeMessageRepository.findById(id).orElseGet(() -> {
            return null;
        });
    }

    public TypeMessage create(TypeMessage input) throws Exception {
        return typeMessageRepository.findByTypemessgaeAndActive(input.getTypemessgae().toUpperCase(), true).orElseGet(() -> {
            /*TypeMessage a = new TypeMessage();
            a.setTypemessgae(input.getTypemessgae().toUpperCase());*/

            input.setActive(true);

            input.setCreation(Instant.now());

            input.setLastUpdate(Instant.now());

            return typeMessageRepository.save(input);
        });
    }


    public TypeMessage update(TypeMessage c) throws Exception {
        typeMessageRepository.findByIdAndActive(c.getId(), true).ifPresent(b -> {
            //b.setTypemessgae(c.getTypemessgae().toUpperCase());
            c.setLastUpdate(Instant.now());
            typeMessageRepository.save(c);
        });
        return c;
    }

    public void disable(long id) throws Exception {
        typeMessageRepository.findById(id).ifPresent(model -> {
            model.setActive(false);
            typeMessageRepository.save(model);
        });
    }

    public void enable(long id) throws Exception {
        typeMessageRepository.findById(id).ifPresent(model -> {
            model.setActive(true);
            typeMessageRepository.save(model);
        });
    }

    public void actionRequest(long id, boolean active) throws Exception {

        typeMessageRepository.findById(id).ifPresent(model -> {

            model.setActive(active);

            typeMessageRepository.save(model);
        });
    }
}
